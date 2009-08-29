package org.ooc.frontend.parser;

import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.SourceReader;

public class VariableAccessParser {

	public static VariableAccess parse(SourceReader sReader, TokenReader reader) {
		int mark = reader.mark();
		
		Token token = reader.peek();
		if(token.isNameToken()) {
			reader.skip();
			return new VariableAccess(token.get(sReader), token);
		}
		
		reader.reset(mark);
		return null;
	}
	
}
