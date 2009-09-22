package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class FunctionCallParser {

	public static FunctionCall parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {
		
		int mark = reader.mark();
		
		Token tName = reader.read();
		if(!tName.isNameToken()) {
			reader.reset(mark);
			return null;
		}
		String name = tName.get(sReader);
		
		String suffix = null;
		if(reader.peek().type == TokenType.TILDE) {
			reader.skip();
			Token tSuff = reader.read();
			if(tSuff.type != TokenType.NAME) {
				throw new CompilationFailedError(sReader.getLocation(tSuff),
				"Expecting suffix after 'functionname~' !");
			}
			suffix = tSuff.get(sReader);
		}

		FunctionCall call = new FunctionCall(name, suffix, tName);
		
		if(!ExpressionListFiller.fill(module, sReader, reader, call.getArguments())) {
			reader.reset(mark);
			return null; // not a function call
		}
		
		return call;
		
	}
	
}
