package org.ooc.frontend.parser;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDeclFromExpr;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class VariableDeclParser {

	public static VariableDecl parse(Module module, SourceReader sReader, TokenReader reader) {
		int mark = reader.mark();
		
		Token tName = reader.peek();
		if(!tName.isNameToken()) {
			reader.reset(mark);
			return null;
		}
		
		Token declStartToken = reader.peek();
		NodeList<VariableDeclAtom> atoms = new NodeList<VariableDeclAtom>(declStartToken);
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
				VariableDeclFromExpr vdfe = new VariableDeclFromExpr(name, expr, isStatic, atomStartToken);
				vdfe.setConst(isConst);
				return vdfe;
			}
			VariableDeclAtom vda = new VariableDeclAtom(name, expr, atomStartToken);
			atoms.add(vda);
			if(reader.peek().type != TokenType.COMMA) break;
			reader.skip();
			reader.skipWhitespace();
		}
		
		if(reader.read().type != TokenType.COLON) {
			reader.reset();
			return null;
		}
		
		boolean isStatic = false;
		String externName = null;
		
		while(true) {
			Token t = reader.peek();
			if(t.type == TokenType.STATIC_KW) {
				isStatic = true;
				reader.skip();
			} else if(t.type == TokenType.EXTERN_KW) {
				externName = ExternParser.parse(sReader, reader);
			} else {
				break;
			}
		}
		
		Type type = TypeParser.parse(module, sReader, reader);
		if(type == null) {
			reader.reset(mark);
			return null;
		}
		if(atoms.size() == 1 && atoms.get(0).getExpression() == null) {
			if(reader.peek().type == TokenType.ASSIGN) {
				reader.skip();
				Expression expr = ExpressionParser.parse(module, sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected expression as an initializer to a variable declaration.");
				}
				atoms.get(0).setExpression(expr);
			}
		}
		
		VariableDecl decl = new VariableDecl(type, isStatic, declStartToken.cloneEnclosing(reader.prev()));
		decl.setExternName(externName);
		decl.getAtoms().addAll(atoms);
		
		return decl;
	}

}
