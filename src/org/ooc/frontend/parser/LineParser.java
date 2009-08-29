package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class LineParser {

	public static boolean fill(SourceReader sReader, TokenReader reader, NodeList<Line> body) throws IOException {
		
		int mark = reader.mark();
		
		reader.skipWhitespace();
		
		while(reader.peek().type == TokenType.LINESEP) reader.skip();
		
		if(!reader.hasNext()) {
			reader.reset(mark);
			return false;
		}
		
		Statement statement = StatementParser.parse(sReader, reader);
		if(statement == null) {
			reader.reset(mark);
			return false;
		}
		body.add(new Line(statement));
		
		while(reader.peek().type == TokenType.L_ARROW) {
			Expression expr = null;
			if(statement instanceof MemberCall) {
				MemberCall memberCall = (MemberCall) statement;
				expr = memberCall.getExpression();
			} else if(statement instanceof Expression) {
				expr = (Expression) statement;
			} else {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Arrows '<-' for chain-calls should be used after member function calls only");
			}
			reader.skip();
			Token startToken = reader.peek();
			FunctionCall otherCall = FunctionCallParser.parse(sReader, reader);
			if(otherCall == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Expected function call after an arrow '<-'");
			}
			statement = new MemberCall(expr, otherCall, startToken);
			body.add(new Line(statement));
		}
		
		if(!(statement instanceof ControlStatement)) {
			Token next = reader.read();
			if(next.type != TokenType.LINESEP) {
				throw new CompilationFailedError(sReader.getLocation(next),
						"Missing semi-colon at the end of a line (got a "+next+" instead)");
			}
		}
		
		return true;
		
	}

	
}
