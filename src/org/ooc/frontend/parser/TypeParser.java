package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class TypeParser {

	public static Type parse(SourceReader sReader, TokenReader reader) throws IOException {
		
		String name = "";
		int pointerLevel = 0;
		int referenceLevel = 0;
		boolean isArray = false;
		
		Token startToken = reader.peek();
		
		//TODO add more checks
		while(reader.hasNext()) {
			Token t = reader.peek();
			if(t.type == TokenType.UNSIGNED) {
				reader.skip();
				name += "unsigned ";
			} else if(t.type == TokenType.SIGNED) {
				reader.skip();
				name += "signed ";
			} else if(t.type == TokenType.LONG) {
				reader.skip();
				name += "long ";
			} else if(t.type == TokenType.STRUCT) {
				reader.skip();
				name += "struct ";
			} else if(t.type == TokenType.UNION) {
				reader.skip();
				name += "union ";
			} else break;
		}
			
		if(reader.peek().type == TokenType.NAME) {
			name += reader.read().get(sReader);
		}
		
		if(name.equals("Func")) {
			FuncType funcType = new FuncType(startToken);
			ArgumentListFiller.fill(sReader, reader, true, funcType.getDecl().getArguments());
			if(reader.peek().type == TokenType.ARROW) {
				reader.read();
				funcType.getDecl().setReturnType(TypeParser.parse(sReader, reader));
				if(funcType.getDecl().getReturnType() == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expected function pointer return type after the arrow '->'");
				}
			}
			return funcType;
		}

		while(reader.peek().type == TokenType.OPEN_SQUAR) {
			reader.skip();
			if(reader.read().type != TokenType.CLOS_SQUAR) {
				return null;
			}
			pointerLevel++;
			isArray = true;
		}
		
		while(reader.peek().type == TokenType.STAR) {
			pointerLevel++;
			reader.skip();
		}
		
		while(reader.peek().type == TokenType.AT) {
			referenceLevel++;
			reader.skip();
		}
		
		if(!name.isEmpty()) {
			Type type = new Type(name.trim(), pointerLevel, referenceLevel, startToken);
			type.setArray(isArray);
			return type;
		}
		return null;
		
	}
	
}
