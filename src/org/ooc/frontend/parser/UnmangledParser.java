package org.ooc.frontend.parser;

import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class UnmangledParser {

	public static String parse(SourceReader sReader, TokenReader reader) throws CompilationFailedError {
		
		String unmangledName = null;
		
		if(reader.peek().type == TokenType.UNMANGLED_KW) {
			reader.skip();
			if(reader.peek().type == TokenType.OPEN_PAREN) {
				reader.skip();
				Token nameToken = reader.read();
				unmangledName = nameToken.get(sReader);
				if(reader.read().type != TokenType.CLOS_PAREN) {
					throw new CompilationFailedError(null,
							"Expected closing parenthesis after unmangled specification, but got "+reader.peek());
				}
			} else {
				unmangledName = "";
			}
		}
		return unmangledName;
	}
	
}
