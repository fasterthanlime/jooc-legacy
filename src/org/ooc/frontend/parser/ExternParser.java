package org.ooc.frontend.parser;

import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ExternParser {

	public static String parse(SourceReader sReader, TokenReader reader) throws CompilationFailedError {
		
		String externName = null;
		
		if(reader.peek().type == TokenType.EXTERN_KW) {
			reader.skip();
			if(reader.peek().type == TokenType.OPEN_PAREN) {
				reader.skip();
				Token nameToken = reader.read();
				if(nameToken.type != TokenType.NAME) {
					throw new CompilationFailedError(null,
							"Expected name in extern specification, but got "+nameToken.type);
				}
				externName = nameToken.get(sReader);
				if(reader.read().type != TokenType.CLOS_PAREN) {
					throw new CompilationFailedError(null,
							"Expected closing parenthesis after extern specification, but got "+reader.peek().type);
				}
			} else {
				externName = "";
			}
		}
		return externName;
	}
	
}
