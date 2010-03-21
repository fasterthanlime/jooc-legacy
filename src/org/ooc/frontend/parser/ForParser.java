package org.ooc.frontend.parser;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.For;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ForParser {

	public static Foreach parseForeach(Module module, SourceReader sReader, TokenReader reader) {

		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type == TokenType.FOR_KW) {
			if(reader.read().type != TokenType.OPEN_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected opening parenthesis after for");
			}
			
			Expression variable = VariableDeclParser.parseSingle(module, sReader, reader);
			if(variable == null) {
				variable = AccessParser.parse(module, sReader, reader);
				if(variable == null) {
					reader.reset(mark);
					return null;
				}
			}
			
			if(reader.read().type != TokenType.IN_KW) {
				reader.reset(mark);
				return null;
			}
			
			Expression collection = ExpressionParser.parse(module, sReader, reader);
			if(collection == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected expression after 'in' keyword in a foreach");
			}
			if(variable instanceof VariableAccess && collection instanceof RangeLiteral) {
				// FIXME not flexible enough
				VariableAccess varAcc = ((VariableAccess) variable);
				variable = new VariableDecl(IntLiteral.type, varAcc.getName(), startToken, module);
			}
			
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected closing parenthesis at the end of a foreach");
			}
			
			Foreach foreach = new Foreach(variable, collection, startToken);
			ControlStatementFiller.fill(module, sReader, reader, foreach.getBody());
			return foreach;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
	public static For parseRegularFor(Module module, SourceReader sReader, TokenReader reader) {

		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type == TokenType.FOR_KW) {
			if(reader.read().type != TokenType.OPEN_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected opening parenthesis after for");
			}
			
			Statement init = StatementParser.parse(module, sReader, reader);
			if(init == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected init statement here, inside a for");
			}
			
			if(reader.read().type != TokenType.LINESEP) {
				reader.reset(mark);
				return null;
			}
			
			Expression test = ExpressionParser.parse(module, sReader, reader);
			if(test == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected test expression here, inside a for");
			}
			
			if(reader.read().type != TokenType.LINESEP) {
				reader.reset(mark);
				return null;
			}
			
			Statement iter = StatementParser.parse(module, sReader, reader);
			if(iter == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected iter statement here, inside a for");
			}
			
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected closing parenthesis at the end of a for");
			}
			
			For for1 = new For(init, test, iter, startToken);
			ControlStatementFiller.fill(module, sReader, reader, for1.getBody());
			return for1;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
