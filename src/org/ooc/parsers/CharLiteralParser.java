package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.text.CharLiteral;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

class CharLiteralParser implements Parser {

	
	public boolean parse(SourceContext manager) throws IOException, SyntaxError {
		
        if(!manager.reader.matches("'", true)) {
        	return false;
        }
        manager.add(readCharLiteral(manager.reader));
		
		return true;
	    
	}
	
	protected static CharLiteral readCharLiteral(SourceReader reader) throws SyntaxError, EOFException {

    	return new CharLiteral(reader.getLocation(), SourceReader.spelled(reader.readCharLiteral()));
        
    }

}
