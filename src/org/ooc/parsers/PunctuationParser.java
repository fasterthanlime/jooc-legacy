package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.numeric.Range;
import org.ooc.nodes.operators.Arrow;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.others.Colon;
import org.ooc.nodes.others.Comma;
import org.ooc.nodes.others.LineSeparator;
import org.ubi.SourceReader;

class PunctuationParser implements Parser {

	
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;

		boolean success;
		
		if (reader.matches("..", true)) {
			
	        context.add(new Range(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches(",", true)) {
	
	        context.add(new Comma(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("->", true)) {
		
	    	context.add(new Arrow(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches(".", true)) {
	
	        context.add(new Dot(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches(":", true)) {
	
	        context.add(new Colon(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches(";", true)) {
	
	        context.add(new LineSeparator(reader.getLocation()));
	        success = true;
	
	    } else if (reader.matches("\\", true)) {
	
	    	// Backslashes should be ignored
	        success = true;
	
	    } else {
	    	
	    	success = false;
	    	
	    }
		
		return success;
		
	}

}
