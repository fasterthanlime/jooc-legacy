package org.ooc.frontend.parser;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NamespaceDecl;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ImportParser {

	public static boolean fill(Module module, SourceReader sReader, TokenReader reader) {

		Token startToken = reader.peek();
		if(startToken.type != TokenType.IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		NamespaceDecl namespace = null;
		NodeList<Import> imports = new NodeList<Import>(startToken);

		Token token = null;
		
		while(true) {
		
			token = reader.read();
			if(token.type == TokenType.LINESEP || token.type == TokenType.INTO_KW) {
				Import imp = new Import(sb.toString(), startToken);
				imports.add(imp);
				break;
			} else if(token.type == TokenType.COMMA) {
				Import imp = new Import(sb.toString(), startToken);
				imports.add(imp);
				sb.setLength(0);
				reader.skipWhitespace();
				startToken = reader.peek();
			} else if(token.type == TokenType.SLASH) {
				sb.append('/');
				if(readMulti(sReader, reader, imports, sb, token)) break;
				if(reader.peek().type == TokenType.COMMA) {
					reader.skip();
				}
			} else if(token.type == TokenType.DOT) {
				throw new CompilationFailedError(sReader.getLocation(token),
						"import my.package.MyClass style deprecated, use import my/package/MyClass instead.");
			} else if(token.type == TokenType.DOUBLE_DOT) {
				sb.append("..");
			} else {
				sb.append(token.get(sReader));
			}
			
		}
		
		if(token.type == TokenType.INTO_KW || reader.peek().type == TokenType.INTO_KW) {
			if(reader.peek().type == TokenType.INTO_KW)
				reader.skip();
			Token nameToken = reader.read();
			if(nameToken.type != TokenType.NAME) {
				throw new CompilationFailedError(sReader.getLocation(token),
					"Expected NAME after `into` keyword.");
			}
			if(namespace != null) {
				throw new CompilationFailedError(sReader.getLocation(token),
					"Only one `into` declaration is allowed per import statement.");
			}
			String nsName = nameToken.get(sReader);
			namespace = module.getNamespace(nsName);
			if(namespace == null) {
			       namespace = new NamespaceDecl(nsName, nameToken, module);
			       module.addNamespace(namespace);
			}
		}

		if(namespace != null) {
			namespace.getImports().addAll(imports);
		} else {
			module.getGlobalImports().addAll(imports);
		}
		
		return true;
		
	}

	private static boolean readMulti(SourceReader sReader, TokenReader reader,
			NodeList<Import> imports, StringBuilder sb,
			Token token) throws CompilationFailedError {
		if(reader.peek().type == TokenType.OPEN_SQUAR) {
			reader.skip();
			StringBuilder innerSb = new StringBuilder();
			Token startToken = reader.peek();
			while(true) {
				reader.skipWhitespace();
				Token innerToken = reader.read();
				if(innerToken.type == TokenType.COMMA) {
					Import imp = new Import(sb.toString() + innerSb.toString(), startToken);
					imports.add(imp);
					innerSb.setLength(0);
					startToken = reader.peek();
				} else if(innerToken.type == TokenType.CLOS_SQUAR) {
					Import imp = new Import(sb.toString() + innerSb.toString(), startToken);
					imports.add(imp);
					break;
				} else {
					innerSb.append(innerToken.get(sReader));
				}
			}
			sb.setLength(0);
			Token next = reader.peek();
			if(next.type == TokenType.LINESEP || next.type == TokenType.INTO_KW) {
				return true;
			} else if(next.type != TokenType.COMMA) {
				throw new CompilationFailedError(sReader.getLocation(next),
						"Unexpected token "+next+" while reading an import");
			}
		}
		return false;
	}
	
}
