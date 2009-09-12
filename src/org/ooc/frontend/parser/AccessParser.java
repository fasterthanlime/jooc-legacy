package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeAccess;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.SourceReader;

public class AccessParser {

	public static VariableAccess parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();
		
		Token token = reader.peek();
		if(token.isNameToken()) {
			Type type = TypeParser.parse(module, sReader, reader);
			if(type != null && type.getTypeParams().size() > 0) {
				return new TypeAccess(type);
			}
			
			reader.reset(mark);
			reader.skip();
			String name = token.get(sReader);
			return new VariableAccess(name, token);
		}
		
		reader.reset(mark);
		return null;
	}
	
}
