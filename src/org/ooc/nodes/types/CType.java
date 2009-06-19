package org.ooc.nodes.types;

import java.io.IOException;

import org.ooc.nodes.others.SyntaxNode;
import org.ooc.parsers.TypeParser;
import org.ubi.FileLocation;

/**
 * Dummy node to specify that a type comes from the C world and not
 * to worry about it., e.g.
 * <code>
 * type SDL_Event;
 * </code>
 * 
 * @author Amos Wenger
 */
public class CType extends SyntaxNode {

	private final String name;

	/**
	 * Default constructor
	 */
	public CType(FileLocation location, String name) {
		
		super(location);
		this.name = name;
		TypeParser.addType(name);
		
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		// Hahahaha. As if.
		
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	
}
