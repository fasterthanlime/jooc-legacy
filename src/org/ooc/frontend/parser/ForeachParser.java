package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ForeachParser {

	public static Foreach parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type == TokenType.FOR_KW) {
			if(reader.read().type != TokenType.OPEN_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected opening parenthesis after for");
			}
			
			Expression variable = VariableDeclParser.parse(module, sReader, reader);
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
				VariableDecl vDecl = new VariableDecl(IntLiteral.type, false, startToken);
				vDecl.getAtoms().add(new VariableDeclAtom(varAcc.getName(), null, startToken));
				variable = vDecl;
			}
			
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected closing parenthesis at the end of a foreach");
			}
			
			Foreach foreach = new Foreach(variable, collection, startToken);
			ControlStatementFiller.fill(module, sReader, reader, foreach);
			return foreach;
			
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
