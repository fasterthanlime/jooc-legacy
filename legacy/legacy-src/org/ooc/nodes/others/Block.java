package org.ooc.nodes.others;

import org.ubi.FileLocation;

/**
 * A block of code, not delimited by anything.
 * Base class for Scopes, etc.
 * 
 * @author Amos Wenger
 */
public class Block extends SyntaxNodeList {

	/**
	 * Default constructor
	 * @param location
	 */
	public Block(FileLocation location) {
		super(location);
	}

}
