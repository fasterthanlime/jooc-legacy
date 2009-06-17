package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.numeric.BooleanLiteral;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Boolean literal, e.g. "true" or "false"
 * 
 * @author Amos Wenger
 */
public class BooleanLiteralParser implements Parser {

	@Override
	public boolean parse(SourceContext context) throws IOException, SyntaxError {
	
		SourceReader reader = context.reader;
    		
    	BooleanLiteral boolLiteral = readBooleanLiteral(reader);
    	if(boolLiteral == null) {
    		return false;
    	}
    	
		context.add(boolLiteral);
    	return true;        	
		
	}

	/**
	 * Read a boolean literal, e.g. "true" or "false" from a reader
	 * @param reader
	 * @return
	 * @throws EOFException
	 * @throws SyntaxError
	 */
	public static BooleanLiteral readBooleanLiteral(SourceReader reader) throws EOFException, SyntaxError {

		FileLocation location = reader.getLocation();
		
		if(reader.matches("true", true) && !Character.isLetterOrDigit(reader.peek())) {
        	
            return new BooleanLiteral(location, true);
            
        } else if(reader.matches("false", true) && !Character.isLetterOrDigit(reader.peek())) {
        	
        	return new BooleanLiteral(location, false);
            
        } else {
        	
        	return null;
        	
        }
		
	}	
	
}
