package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.types.StructDef;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * A parser of struct definitions 
 * 
 * @author Amos Wenger
 */
public class StructDefParser implements Parser {
	
	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;

		boolean result;
		
		if (reader.matches("struct", true) && reader.hasWhitespace(true)) {
			
			try {
			
				reader.skipWhitespace();
		        String name = reader.readName();
		        
		        reader.skipWhitespace();
		        if(reader.matches("{", false)) {
		        	
		        	String block = reader.readBlock('{', '}');
		        	if(!reader.matches(";", true)) {
			            reader.err("Expected semi-colon after struct definition...");
			        }
		        	context.add(new StructDef(reader.getLocation(), name, block));
		        	result = true;
		        	
		        } else {
		        	
		        	result = false;
		        	
		        }
	            
			} catch(SyntaxError e) {
				
				//e.printStackTrace();
				result = false;
				
			}

        } else {
        	
        	result = false;
        	
        }
		
		return result;
		
	}

}
