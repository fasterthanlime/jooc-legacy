package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.array.Subscript;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.others.Parenthesis;
import org.ubi.SourceReader;

/**
 * Blocks parser, e.g. {}, (). []
 * 
 * @author Amos Wenger
 */
public class BlocksParser implements Parser {

	
	public boolean parse(SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		boolean success;
		
		if (reader.matches("{", true)) {
			
	        context.open(new Scope(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("}", true)) {
	
	        context.close(Scope.class);
	        success = true;
	
	    } else if (reader.matches("(", true)) {
	
	        context.open(new Parenthesis(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches(")", true)) {
	
	        context.close(Parenthesis.class);
	        success = true;
	
	    } else if (reader.matches("[", true)) {
	
	        context.open(new Subscript(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("]", true)) {
	    	
	        context.close(Subscript.class);
	        success = true;
	        
	    } else {
	    	
	    	success = false;
	    	
	    }
		
		return success;
		
	}

}
