package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;

import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.numeric.DoubleLiteral;
import org.ooc.nodes.numeric.FloatLiteral;
import org.ooc.nodes.numeric.IntLiteral;
import org.ooc.nodes.numeric.NumberLiteral;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Parse number literals, (e.g. int, floats, doubles, etc.)
 * @author Amos Wenger
 */
public class NumberLiteralParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
        NumberLiteral numberLiteral = readNumberLiteral(reader);
		if(numberLiteral == null) {
			return false;
		}
		
		context.add(numberLiteral);
        return true;
		
	}
	
	/**
	 * Read a number literal from the reader
	 * @param reader
	 * @return
	 * @throws SyntaxError
	 * @throws EOFException
	 */
    public static NumberLiteral readNumberLiteral(SourceReader reader) throws EOFException {

    	NumberLiteral result;
    	
    	if(reader.matches("0x", true)) {
        	String literal = reader.readMany("0123456789abcdef", "_", true);
        	if(literal.isEmpty()) {
        		throw new CompilationFailedError(reader.getLocation(), "Invalid hex literal format: must be of syntax 0x0123456789abcdef for example.");
        	}
			return new IntLiteral(reader.getLocation(), new BigInteger(literal, 16).intValue());
        }
    	
        String literal = reader.readMany("0123456789", "_", true);
        if(literal.isEmpty()) {
        	return null; // Too bad..
        }
        
        if(!reader.matches("..", false) && reader.matches(".", true)) {
            String floatingPart = reader.readMany("0123456789", "_", true);
            if(reader.matches("f", true)) {
                result = new FloatLiteral(reader.getLocation(), Float.parseFloat(literal+"."+floatingPart+"f"));
            } else {
				// By default, is double.
				// l suffix means long double. But heck, that's another story.
            	result = new DoubleLiteral(reader.getLocation(), Double.parseDouble(literal+"."+floatingPart));
            }
        } else {
        	if(reader.matches("f", true)) {
                result = new FloatLiteral(reader.getLocation(), Float.parseFloat(literal));
            } else if(reader.matches("d", true)) {
            	result = new DoubleLiteral(reader.getLocation(), Double.parseDouble(literal));
            } else {
            	result = new IntLiteral(reader.getLocation(), Integer.parseInt(literal));
            }
        }
        
        return result;
        
    }

}
