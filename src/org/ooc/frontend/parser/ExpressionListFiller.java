package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ExpressionListFiller {
	
	public static boolean fill(SourceReader sReader, TokenReader reader,
			NodeList<Expression> list) throws IOException {
		return fill(sReader, reader, list, TokenType.OPEN_PAREN, TokenType.CLOS_PAREN);
	}

	public static boolean fill(SourceReader sReader, TokenReader reader,
			NodeList<Expression> list, byte opening, byte closing) throws IOException {

		int mark = reader.mark();
		
		if(!reader.hasNext()) return false;
		
		if(reader.read().type != opening) {
			reader.reset(mark);
			return false;
		}
		
		boolean comma = false;
		while(true) {
			
			if(reader.peekWhiteless().type == closing) {
				reader.skipWhitespace();
				reader.skip(); // skip the ')'
				break;
			}
			if(comma) {
				if(reader.readWhiteless().type != TokenType.COMMA) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected comma between arguments of a function call");
				}
			} else {
				reader.skipWhitespace();
				Expression expr = ExpressionParser.parse(sReader, reader);
				if(expr == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expected expression as argument of function call");
				}
				list.add(expr);
			}
			comma = !comma;
			
		}
		
		return true;
		
	}
	
}
