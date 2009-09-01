package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Conditional;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ConditionalParser {

	public static Conditional parse(Module module,
			SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type != TokenType.WHILE_KW && startToken.type != TokenType.IF_KW) {
			reader.reset(mark);
			return null;
		}
		
		if(reader.read().type != TokenType.OPEN_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev()),
				"Expected opening parenthesis after "+startToken.get(sReader));
		}
		
		Expression condition = ExpressionParser.parse(module, sReader, reader);
		if(condition == null) {
			throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Expected expression as while condition");
		}
		
		if(reader.read().type != TokenType.CLOS_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev()),
				"Expected closing parenthesis after expression of an "+startToken.get(sReader));
		}
		
		Conditional statement;
		if(startToken.type == TokenType.WHILE_KW) {
			statement = new While(condition, startToken);
		} else if(startToken.type == TokenType.IF_KW) {
			statement = new If(condition, startToken);
		} else {
			reader.reset(mark);
			return null;
		}
		
		ControlStatementFiller.fill(module, sReader, reader, statement);
		
		return statement;
		
	}

	
}
