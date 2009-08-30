package org.ooc.frontend.parser;

import java.util.List;

import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.SourceReader;

public class TypeParamParser {

	public static void parse(SourceReader sReader, TokenReader reader,
			List<GenericType> typeParams) {
		while(reader.peek().type != TokenType.GREATERTHAN) {
			Token nameTok = reader.read();
			typeParams.add(new GenericType(nameTok.get(sReader), nameTok));
			if(reader.peek().type != TokenType.COMMA) break;
			reader.skip();
		}
		reader.skip();
	}
	
}
