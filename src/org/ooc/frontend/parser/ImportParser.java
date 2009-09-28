package org.ooc.frontend.parser;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ImportParser {

	public static boolean fill(SourceReader sReader, 
			TokenReader reader, NodeList<Import> imports) {

		Token startToken = reader.peek();
		if(startToken.type != TokenType.IMPORT_KW) {
			return false;
		}
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
		
			Token token = reader.read();
			if(token.type == TokenType.LINESEP) {
				Import imp = new Import(sb.toString(), startToken);
				imports.add(imp);
				break;
			}
			if(token.type == TokenType.COMMA) {
				Import imp = new Import(sb.toString(), startToken);
				imports.add(imp);
				sb.setLength(0);
				startToken = reader.peek();
			} else if(token.type == TokenType.DOT) {
				throw new CompilationFailedError(sReader.getLocation(token),
						"import my.package.MyClass style deprecated, use import my/package/MyClass instead.");
			} else if(token.type == TokenType.SLASH) {
				sb.append('/');
				if(readMulti(sReader, reader, imports, sb, token)) break;
			} else if(token.type == TokenType.DOUBLE_DOT) {
				sb.append("..");
			} else {
				sb.append(token.get(sReader));
			}
			
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
			Token next = reader.read();
			if(next.type == TokenType.LINESEP) {
				return true;
			} else if(next.type != TokenType.COMMA) {
				throw new CompilationFailedError(sReader.getLocation(token),
						"Unexpected token "+token+" while reading an import");
			}
		}
		return false;
	}
	
}
