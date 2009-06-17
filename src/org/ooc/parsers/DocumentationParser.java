package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.doc.MultiLineComment;
import org.ooc.nodes.doc.OocDocComment;
import org.ooc.nodes.doc.SingleLineComment;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * 
 * @author Amos Wenger
 */
class DocumentationParser implements Parser {

	final private StringBuilder block = new StringBuilder();
	
	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;	
		boolean success;
		
		if(reader.matches("//", true)) {
			
	        context.add(new SingleLineComment(reader.getLocation(), reader.readLine()));
	        success = true;
	
	    } else if(reader.matches("/**", false)) {
	    	
	    	int index = reader.mark();
	    	StringBuilder before = new StringBuilder();
	    	while(true) {
	    		if(reader.backMatches('\n', false)) {
	    			before.append("\n");
	    		} else if(reader.backMatches(' ', false) || reader.backMatches('\t', false)) {
	    			// Just ignore
	    		} else {
	    			break;
	    		}
	    		reader.rewind(1);
	    	}
	    	reader.reset(index);
	    	
	    	block.setLength(0);
        	try {
				block.append(reader.readBlock("/**", "*/", '\0'));
			} catch (SyntaxError e) {
				return false;
			}
			
	        String after = reader.readMany("\n\t ", "", true);
	        context.add(new OocDocComment(reader.getLocation(), before.toString(), block.toString(), after));
	        
	        success = true;
	
	    } else if(reader.matches("/*", false)) {
	 
	    	int index = reader.mark();
	    	StringBuilder before = new StringBuilder();
	    	while(true) {
	    		if(reader.backMatches('\n', false)) {
	    			before.append("\n");
	    		} else if(reader.backMatches(' ', false) || reader.backMatches('\t', false)) {
	    			// Just ignore
	    		} else {
	    			break;
	    		}
	    		reader.rewind(1);
	    	}
	    	reader.reset(index);
	    	
	    	block.setLength(0);
        	try {
				block.append(reader.readBlock("/*", "*/", '\0'));
			} catch (SyntaxError e) {
				return false;
			}
			
	        String after = reader.readMany("\n\t ", "", true);
	        context.add(new MultiLineComment(reader.getLocation(), before.toString(), block.toString(), after));

	        success = true;
	
	    } else {
	    	
	    	success = false;
	    	
	    }
		
		return success;
	}

}

