package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class ReturnParser {

	public static Return parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type == TokenType.RETURN_KW) {
			Expression expr = ExpressionParser.parse(module, sReader, reader);
			if(expr == null) {
				return new Return(startToken);
			}
			return new ValuedReturn(expr, startToken);
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
