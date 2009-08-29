package org.ooc.nodes.types;

import java.io.IOException;

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
public class CType extends Type {

	/**
	 * Default constructor
	 * @param location
	 * @param model
	 */
	public CType(FileLocation location, Type model) {
		
		super(location, model.getContext(), model.name, model.getPointerLevel(), model.getArrayLevel());
		isResolved = true;
		TypeParser.addType(name);
		
	}

	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		// Hahahaha. As if.
		
	}
	
}
