package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.control.Else;
import org.ooc.nodes.control.For;
import org.ooc.nodes.control.Goto;
import org.ooc.nodes.control.If;
import org.ubi.SourceReader;

class ControlsParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		boolean result = false;
		
		if(reader.matches("for", true)) {
			
            reader.skipWhitespace();
            if(reader.matches("(", true)) {
                context.open(new For(reader.getLocation()));
                result = true;
            }
            
        } if(reader.matches("goto", true)) {
        	
            reader.skipWhitespace();
            String label = reader.readName();
            context.add(new Goto(reader.getLocation(), label));
            result = true;
            
        } else if(reader.matches("if", true)) {
        	
        	reader.skipWhitespace();
        	if(reader.matches("(", true)) {
                context.open(new If(reader.getLocation()));
                result = true;
            }
        	
        } else if(reader.matches("else", true)) {
        	
        	reader.skipWhitespace();
        	context.add(new Else(reader.getLocation()));
            result = true;
                
        }
		
		return result;
		
	}

}
