package org.ooc.frontend.parser;

import org.ooc.frontend.model.Case;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.Match;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class MatchParser {

	public static Match parse(Module module, SourceReader sReader, TokenReader reader) {
		
		Token startToken = reader.peek();
		if(startToken.type != TokenType.MATCH_KW) return null;
		reader.skip();

		Expression expr = ExpressionParser.parse(module, sReader, reader, true);
		
		if(reader.peek().type != TokenType.OPEN_BRACK) {
			throw new CompilationFailedError(sReader.getLocation(reader.peek()), "Expected a '{' after match.");
		}
		reader.skip();
		
		Match match = new Match(expr, startToken);
		
		while(true) {
			
			reader.skipWhitespace();
			
			Token token = reader.peek();
			if(token.type == TokenType.CLOS_BRACK) {
				reader.skip();
				break;
			}
			boolean isFallthrough = false;
			if(token.type == TokenType.FALLTHR_KW) {
				isFallthrough = true;
				reader.skip();
				reader.skipWhitespace();
				token = reader.peek();
				System.out.println("Just read fallthrough, next token is "+token);
			}
			if(token.type == TokenType.CASE_KW) {
				reader.skip();
				Expression caseExpr = ExpressionParser.parse(module, sReader, reader, true);
				reader.skipWhitespace();
				if(reader.peek().type != TokenType.DOUBLE_ARROW) {
					throw new CompilationFailedError(sReader.getLocation(token), "Unexpected token "+reader.peek()
							+" Expected a '=>' after a case.");
				}
				reader.skip();
				Case case1 = new Case(caseExpr, match, isFallthrough, token);
				LineParser.fill(module, sReader, reader, case1.getBody());
				match.getCases().add(case1);
				continue;
			}
			
			throw new CompilationFailedError(sReader.getLocation(token), "Unexpected token "+token
					+" Expected either a '}' or a case.");
			
		}
		
		return match;
		
	}
	
}
