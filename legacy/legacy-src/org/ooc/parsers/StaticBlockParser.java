package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.others.StaticBlock;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

/**
 * Static block parser.
 * 
 * @author Amos Wenger
 */
public class StaticBlockParser implements Parser {

	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		FileLocation location = reader.getLocation();
		
		if(reader.matches("static", true) && reader.hasWhitespace(true) && reader.matches("{", true)) {
	    	
	    	context.open(new StaticBlock(location));
	    	return true;
	    	
	    }
		
		return false;
	
	}
	
}
