package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.functions.FunctionCall;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Parse a function call
 * 
 * @author Amos Wenger
 */
public class FunctionCallParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException {

    	FunctionCall funcCall = FunctionCallParser.readFunctionCall(context.reader);
    	if(funcCall == null) {
    		return false;
    	}
    	
		context.open(funcCall);
        return true;
		
	}

	/**
	 * Read a function call
	 * @param sourceReader
	 * @return
	 * @throws EOFException
	 * @throws SyntaxError
	 */
	public static FunctionCall readFunctionCall(SourceReader sourceReader) throws EOFException {

        sourceReader.skipWhitespace();
        String name = sourceReader.readName();
        sourceReader.skipWhitespace();
        if(!sourceReader.matches("(", true)) {
            return null;
        }
        return new FunctionCall(sourceReader.getLocation(), name);
		
	}	
	
}
