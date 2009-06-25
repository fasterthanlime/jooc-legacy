package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.text.StringLiteral;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * A string literal parser, e.g. parse things of the form "Blah\ttabbed!\n"
 * 
 * @author Amos Wenger
 */
public class StringLiteralParser implements Parser {
	
	
	public boolean parse(final SourceContext context) throws IOException, SyntaxError {

		SourceReader reader = context.reader;
		
		if(!reader.matches("\"", true)) {
            return false;
        }
		
	    context.add(readStringLiteral(reader));
	    return true;
	    
	}
	
	/**
	 * Read a string literal from reader
	 * @param reader
	 * @return
	 * @throws SyntaxError
	 * @throws EOFException
	 */
	public static StringLiteral readStringLiteral(SourceReader reader) throws SyntaxError, EOFException {
		
        return new StringLiteral(reader.getLocation(), reader.readStringLiteral());
        
    }

}
