package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.others.EnumNode;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * A simple enum parser, e.g.
 * <code>
 * enum EnumName {
 *   VALUE_1,
 *   VALUE_2,
 *   VALUE_3, // last ',' optional but tolerated
 * }
 * </code>
 * 
 * @author Amos Wenger
 */
public class EnumParser implements Parser {

	@Override
	public boolean parse(SourceContext context) throws IOException, SyntaxError {

		SourceReader reader = context.reader;
		
		if (reader.matches("enum", true) && reader.hasWhitespace(true)) {
			
			String name = reader.readName();
			if(name.isEmpty()) {
				reader.err("Expected a name after the 'enum' keyword!");
			}
			
			reader.skipWhitespace();
			
			if(!reader.matches("{", true)) {
				reader.err("Expected an opening brace after 'enum "+name+"'");
			}
			
	        context.open(new EnumNode(reader.getLocation(), name));
	        System.out.println("Just opened an EnumNode at "+reader.getLocation());
	        return true;
	
	    }
		
		return false;
		
	}
	
}
