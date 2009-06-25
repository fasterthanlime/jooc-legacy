package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.others.NullLiteral;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Parse null literals 
 * 
 * @author Amos Wenger
 */
public class NullLiteralParser implements Parser {

	
	public boolean parse(SourceContext context) throws IOException, SyntaxError {

		NullLiteral nullLiteral = readNullLiteral(context.reader);
		if(nullLiteral == null) {
			return false;
		}
		
		context.add(nullLiteral);
		return true;
		
	}

	/**
	 * Parse a null literal, ie. "null" or "NULL"
	 * @param reader
	 * @return
	 * @throws SyntaxError
	 * @throws EOFException
	 */
	public static NullLiteral readNullLiteral(SourceReader reader) throws SyntaxError, EOFException {

		FileLocation location = reader.getLocation();
		
		if((reader.matches("null", true) || reader.matches("NULL", true)) && !Character.isLetterOrDigit(reader.peek())) {
			return new NullLiteral(location);
		}
			
		return null;
			
	}

}
