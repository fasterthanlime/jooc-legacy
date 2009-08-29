package org.ooc.frontend.parser;

import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class CommentParser {

	public static Visitable parse(SourceReader sReader, TokenReader reader) {
		
		Token token = reader.peek();
		if(token.type == TokenType.OOCDOC) {
			reader.skip();
			return new OocDocComment(token.get(sReader), token); // FIXME add oocdoc tags parsing (me=lazy)
		}
		
		return null;
		
	}
	
}
