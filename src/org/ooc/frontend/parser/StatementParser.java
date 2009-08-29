package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.FlowControl;
import org.ooc.frontend.model.Conditional;
import org.ooc.frontend.model.Else;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.FlowControl.Mode;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class StatementParser {

	public static Statement parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Foreach foreach = ForeachParser.parse(sReader, reader);
		if(foreach != null) return foreach;
		
		Conditional conditional = ConditionalParser.parse(sReader, reader);
		if(conditional != null) return conditional;
		
		if(reader.peek().type == TokenType.ELSE_KW) {
			Else else1 = new Else(reader.read());
			ControlStatementFiller.fill(sReader, reader, else1);
			return else1;
		}
		
		if(reader.peek().type == TokenType.BREAK_KW) return new FlowControl(Mode.BREAK, reader.read());
		if(reader.peek().type == TokenType.CONTINUE_KW) return new FlowControl(Mode.CONTINUE, reader.read());
		
		Return ret = ReturnParser.parse(sReader, reader);
		if(ret != null) return ret;
		
		Expression expression = ExpressionParser.parse(sReader, reader);
		if(expression != null) return expression;
		
		reader.reset(mark);
		return null;
		
	}
	
}
