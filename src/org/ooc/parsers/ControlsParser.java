package org.ooc.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.control.Else;
import org.ooc.nodes.control.For;
import org.ooc.nodes.control.Goto;
import org.ooc.nodes.control.If;
import org.ooc.nodes.others.RawCode;
import org.ooc.nodes.others.VersionBlock;
import org.ooc.nodes.others.VersionBlock.Version;
import org.ubi.CompilationFailedError;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

class ControlsParser implements Parser {

	
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		FileLocation location = reader.getLocation();
		
		int mark = reader.mark();
		
		if(reader.matches("for", true)) {
			
            reader.skipWhitespace();
            if(reader.matches("(", true)) {
                context.open(new For(location));
                return true;
            }
            
        } 
		
		reader.reset(mark);
		if(reader.matches("goto", true)) {
        	
            reader.skipWhitespace();
            String label = reader.readName();
            context.add(new Goto(location, label));
            return true;
            
        }
		
		reader.reset(mark);
		if(reader.matches("case", true)) {
        	
            reader.skipWhitespace();
            context.add(new RawCode(location, "case "));
            return true;
            
        }
		
		reader.reset(mark);
		if(reader.matches("if", true)) {
        	
        	reader.skipWhitespace();
        	if(reader.matches("(", true)) {
                context.open(new If(location));
                return true;
            }
        	
        }
		
		reader.reset(mark);
		if(reader.matches("else", true)) {
        	
        	reader.skipWhitespace();
        	context.add(new Else(location));
            return true;
                
        }
		
		reader.reset(mark);
		if(reader.matches("version", true)) {
        	
        	reader.skipWhitespace();
        	if(reader.matches("(", true)) {
        		
        		List<Version> versions = new ArrayList<Version>();
        	
        		while(true) {
        		
	        		reader.skipWhitespace();
	        		boolean inverse = reader.matches("!", true);
	        		reader.skipWhitespace();
	        		String name = reader.readName();
	        		reader.skipWhitespace();
	        		versions.add(new Version(name, inverse));
	        		
	        		if(reader.matches(")", true)) {
	        			break;
	        		} else if(!reader.matches(",", true)) {
	        			throw new CompilationFailedError(location, 
	        					"Expected either a comma or a closing parenthesis, got a '"+
	        					SourceReader.spelled(reader.peek())+"'");
	        		}
        		
        		}
        		
        		if(reader.hasWhitespace(true) && reader.matches("{", true)) {
        			context.open(new VersionBlock(location, versions));
        			return true;
        		}
        		
        	}
        	
        }
		
		return false;
		
	}

}
