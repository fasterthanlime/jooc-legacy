package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.types.CType;
import org.ooc.nodes.types.Type;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * @see CType
 * 
 * @author Amos Wenger
 */
public class CTypeParser implements Parser {

	
	public boolean parse(final SourceContext context) throws IOException, SyntaxError {

		SourceReader reader = context.reader;
		
		if(reader.matches("ctype", true) && reader.hasWhitespace(true)) {
			
			readCTypeName(context, reader);
			
			reader.skipWhitespace();
			
			while(reader.matches(",", true)) {
				
				reader.skipWhitespace();
				readCTypeName(context, reader);
				reader.skipWhitespace();
				
			}
			
			if(!reader.matches(";", true)) {
				throw new CompilationFailedError(reader.getLocation(), "Expected a semi-colon ';' after 'ctype' keyword");
			}
			return true;
		}
		
		return false;
		
	}

	protected void readCTypeName(final SourceContext context, SourceReader reader)
			throws EOFException, CompilationFailedError {
		
		Type type = Type.read(context, reader);
		if(type == null) {
			throw new CompilationFailedError(reader.getLocation(), "Expected a type after 'ctype' keyword.");
		}
		context.add(new CType(reader.getLocation(), type));
		
	}
	
}
