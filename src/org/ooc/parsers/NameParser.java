package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.others.Name;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

class NameParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;

		boolean result;
		
        try {
        	
        	reader.skipWhitespace();
        	FileLocation location = reader.getLocation();
            String name = reader.readName();
            if(name.isEmpty()) {
                reader.err("Expected valid C identifier");
            }
            
            context.add(new Name(location, name));
            result = true;
            
        } catch(SyntaxError e) {
        	
            result = false;
            
        }
        
        return result;
		
	}

}
