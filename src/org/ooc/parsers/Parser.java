package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ubi.SyntaxError;

/**
 * 
 * @author Amos Wenger
 */
public interface Parser {

	/**
	 * Parse a node.
	 * @return true if successfully parsed "something" (and probably modified source), false otherwise
	 */
	public boolean parse(SourceContext context) throws IOException, SyntaxError;
	
}
