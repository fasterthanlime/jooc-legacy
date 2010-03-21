package org.ooc.frontend.parser;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class VariableDeclParser {

	public static Node parseMulti(Module module, SourceReader sReader, TokenReader reader) {
		int mark = reader.mark();
		
		Token tName = reader.peek();
		if(!tName.isNameToken()) {
			reader.reset(mark);
			return null;
		}
		
		Token declStartToken = reader.peek();
		NodeList<VariableDecl> decls = new NodeList<VariableDecl>(declStartToken);
		Token atomStartToken;
		while((atomStartToken = reader.peek()).isNameToken()) {
			String name = reader.read().get(sReader);
			Expression expr = null;
			if(reader.peek().type == TokenType.ASSIGN) {
				reader.skip();
				expr = ExpressionParser.parse(module, sReader, reader, true);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected expression as an initializer to a variable declaration.");
				}
			} else if(reader.peek().type == TokenType.DECL_ASSIGN) {
				reader.skip();
				
				boolean isConst = false, isStatic = false;
				while(true) {
					Token kw = reader.peek();
					if(kw.type == TokenType.CONST_KW) {
						reader.skip();
						isConst = true;
					} else if(kw.type == TokenType.STATIC_KW) {
						reader.skip();
						isStatic = true;
					} else break;
				}
				
				expr = ExpressionParser.parse(module, sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected expression as an initializer to a variable declaration.");
				}
				VariableDecl vdfe = new VariableDecl(null, name, expr, atomStartToken, module);
				vdfe.setConst(isConst);
				vdfe.setStatic(isStatic);
				return vdfe;
			}
			VariableDecl vd = new VariableDecl(null, name, expr, atomStartToken, module);
			decls.add(vd);
			if(reader.peek().type != TokenType.COMMA) break;
			reader.skip();
			reader.skipWhitespace();
		}
		
		if(reader.read().type != TokenType.COLON) {
			reader.reset(mark);
			return null;
		}
		
		boolean isStatic = false;
		boolean isProto = false;
		String externName = null;
		String unmangledName = null;
		
		while(true) {
			Token t = reader.peek();
			if(t.type == TokenType.STATIC_KW) {
				isStatic = true;
				reader.skip();
			} else if(t.type == TokenType.PROTO_KW) {
				isProto = true;
				reader.skip();
			} else if(t.type == TokenType.EXTERN_KW) {
				externName = ExternParser.parse(sReader, reader);
			} else if(t.type == TokenType.UNMANGLED_KW) {
				unmangledName = UnmangledParser.parse(sReader, reader);
			} else {
				break;
			}
		}
		
		Type type = TypeParser.parse(module, sReader, reader);
		if(type == null) {
			reader.reset(mark);
			return null;
		}
		if(decls.size() == 1 && decls.getFirst().getExpression() == null) {
			if(reader.peek().type == TokenType.ASSIGN) {
				reader.skip();
				Expression expr = ExpressionParser.parse(module, sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected expression as an initializer to a variable declaration.");
				}
				decls.getFirst().setExpression(expr);
			}
		}
		
		for(VariableDecl decl : decls) {
			decl.setType(type);
			decl.setStatic(isStatic);
			decl.setProto(isProto);
			decl.setExternName(externName);
			decl.setUnmangledName(unmangledName);
		}
		
		if(decls.size() == 1) {
			return decls.getFirst();
		}
		return decls;
	}

	public static VariableDecl parseSingle(Module module, SourceReader sReader,
			TokenReader reader) {
		
		int mark = reader.mark();
		
		Node node = parseMulti(module, sReader, reader);
		if(node == null || !(node instanceof VariableDecl)) {
			reader.reset(mark);
			return null;
		}
		return (VariableDecl) node;
		
	}

}
