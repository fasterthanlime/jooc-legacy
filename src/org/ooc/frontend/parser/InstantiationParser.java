package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class InstantiationParser {

	public static Instantiation parse(SourceReader sReader, TokenReader reader) throws IOException {

		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type == TokenType.NEW_KW) {
			Type type = TypeParser.parse(sReader, reader);
			if(type != null) {
				Instantiation inst = null;
				String suffix = "";
				if(reader.peek().type == TokenType.TILDE) {
					reader.skip();
					Token tSuffix = reader.read();
					if(tSuffix.type != TokenType.NAME) {
						throw new CompilationFailedError(sReader.getLocation(tSuffix),
								"Expected suffix after '~' when deciding which constructor to use.");
					}
					suffix = tSuffix.get(sReader);
				}
				inst = new Instantiation(type, suffix, startToken);
				ExpressionListFiller.fill(sReader, reader, inst.getArguments());
				return inst;
			}
			Instantiation inst = new Instantiation(startToken);
			ExpressionListFiller.fill(sReader, reader, inst.getArguments());
			return inst;
		}
		
		reader.reset(mark);
		return null;
		
	}
	
}
