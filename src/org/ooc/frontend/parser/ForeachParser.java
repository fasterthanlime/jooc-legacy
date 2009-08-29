package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ForeachParser {

	public static Foreach parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type == TokenType.FOR_KW) {
			if(reader.read().type != TokenType.OPEN_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected opening parenthesis after for");
			}
			
			VariableDecl variable = VariableDeclParser.parse(sReader, reader);
			if(variable == null) {
				reader.reset(mark);
				return null;
			}
			
			if(reader.read().type != TokenType.IN_KW) {
				reader.reset(mark);
				return null;
			}
			
			Expression collection = ExpressionParser.parse(sReader, reader);
			if(collection == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected expression after 'in' keyword in a foreach");
			}
			
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected closing parenthesis at the end of a foreach");
			}
			
			Foreach foreach = new Foreach(variable, collection, startToken);
			ControlStatementFiller.fill(sReader, reader, foreach);
			return foreach;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
