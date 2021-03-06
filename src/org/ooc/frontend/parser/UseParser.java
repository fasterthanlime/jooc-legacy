package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.BuildParams;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ooc.middle.UseDef;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class UseParser {

	public static boolean fill(SourceReader sReader, TokenReader reader,
			NodeList<Use> uses, BuildParams params) throws CompilationFailedError, IOException {
		
		Token startToken = reader.peek();
		if(startToken.type != TokenType.USE_KW) return false;
		reader.skip();
		
		StringBuilder sb = new StringBuilder();
		
		while(true) {
			
			Token token = reader.read();
			if(token.type == TokenType.LINESEP) {
				UseDef useDef = UseDefParser.parse(sb.toString(), sReader, token, params);
				uses.add(new Use(useDef, startToken));
				break;
			}
			if(token.type == TokenType.COMMA) {
				UseDef useDef = UseDefParser.parse(sb.toString(), sReader, token, params);
				uses.add(new Use(useDef, startToken));
				sb.setLength(0);
			} else {
				sb.append(token.get(sReader));
			}
			
		}
		return true;
		
	}

}
