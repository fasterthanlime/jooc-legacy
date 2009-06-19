package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.types.CType;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * @see CType
 * 
 * @author Amos Wenger
 */
public class CTypeParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException, SyntaxError {

		SourceReader reader = context.reader;
		
		if(reader.matches("ctype", true) && reader.hasWhitespace(true)) {
			String name = reader.readName();
			if(name.isEmpty()) {
				throw new CompilationFailedError(reader.getLocation(), "Expected a type name after 'ctype' keyword.");
			}
			context.add(new CType(reader.getLocation(), name));
			reader.skipWhitespace();
			if(!reader.matches(";", true)) {
				throw new CompilationFailedError(reader.getLocation(), "Expected a comma ';' after 'ctype' keyword");
			}
			return true;
		}
		
		return false;
		
	}
	
}
