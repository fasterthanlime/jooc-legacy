package org.ooc.frontend.parser;

import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ControlStatementFiller {

	public static void fill(Module module, SourceReader sReader,
			TokenReader reader, NodeList<Line> body) {
		
		boolean hasBrack = false;
		if(reader.peek().type == TokenType.OPEN_BRACK) {
			reader.skip();
			hasBrack = true;
		}
		
		if(hasBrack) {
			while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
				if(reader.skipWhitespace()) continue;
				if(!LineParser.fill(module, sReader, reader, body)) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Expected line in code block");
				}
			}
			reader.skip(); // the closing bracket
		} else {
			if(!LineParser.fill(module, sReader, reader, body)) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
				"Expected line inside of bracket-less block");
			}
		}
		
	}
	
}
