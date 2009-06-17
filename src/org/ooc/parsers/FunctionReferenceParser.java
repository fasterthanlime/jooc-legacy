package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.reference.FunctionReference;
import org.ubi.SyntaxError;

/**
 * Parses function references
 * 
 * @author Amos Wenger
 */
public class FunctionReferenceParser implements Parser {

	@Override
	public boolean parse(SourceContext context) throws IOException, SyntaxError {

		if(context.reader.matches("@", true)) {
			String name = context.reader.readName();
			if(name.isEmpty()) {
				context.err("Expected function name after '@'");
			}
			context.add(new FunctionReference(context.reader.getLocation(), name));
			return true;
		}
		
		return false;
		
	}

}
