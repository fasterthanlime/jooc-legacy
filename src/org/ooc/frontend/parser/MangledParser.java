package org.ooc.frontend.parser;

import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class MangledParser {

	public static String parse(SourceReader sReader, TokenReader reader) throws CompilationFailedError {
		
		String mangledName = null;
		
		if(reader.peek().type == TokenType.MANGLED_KW) {
			reader.skip();
			if(reader.peek().type == TokenType.OPEN_PAREN) {
				reader.skip();
				Token nameToken = reader.read();
				mangledName = nameToken.get(sReader);
				if(reader.read().type != TokenType.CLOS_PAREN) {
					throw new CompilationFailedError(null,
							"Expected closing parenthesis after mangled specification, but got "+reader.peek());
				}
			} else {
				mangledName = "";
			}
		}
		return mangledName;
	}
	
}
