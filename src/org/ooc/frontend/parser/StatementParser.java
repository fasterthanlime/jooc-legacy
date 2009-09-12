package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Block;
import org.ooc.frontend.model.Conditional;
import org.ooc.frontend.model.Else;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FlowControl;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.FlowControl.Mode;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class StatementParser {

	public static Statement parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Foreach foreach = ForeachParser.parse(module, sReader, reader);
		if(foreach != null) return foreach;
		
		Conditional conditional = ConditionalParser.parse(module, sReader, reader);
		if(conditional != null) return conditional;
		
		if(reader.peek().type == TokenType.ELSE_KW) {
			Else else1 = new Else(reader.read());
			ControlStatementFiller.fill(module, sReader, reader, else1);
			return else1;
		}
		
		if(reader.peek().type == TokenType.BREAK_KW) return new FlowControl(Mode.BREAK, reader.read());
		if(reader.peek().type == TokenType.CONTINUE_KW) return new FlowControl(Mode.CONTINUE, reader.read());
		
		Return ret = ReturnParser.parse(module, sReader, reader);
		if(ret != null) return ret;
		
		Expression expression = ExpressionParser.parse(module, sReader, reader);
		if(expression != null) return expression;
		
		if(reader.peek().type == TokenType.OPEN_BRACK) {
			Block block = new Block(reader.peek());
			reader.skip();
			while(reader.peek().type != TokenType.CLOS_BRACK) {
				if(!LineParser.fill(module, sReader, reader, block.getBody())) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expected lines or '}' in block, but got "+reader.peek());
				}
			}
			reader.skip(); // skip the closing bracket.
			return block;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
