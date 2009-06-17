package org.ooc.nodes.others;

import org.ubi.FileLocation;

/**
 * Base class for Comma and LineSeparator
 * 
 * @author Amos Wenger
 */
public abstract class Separator extends SyntaxNode {

	/**
	 * Create a new separator
	 * @param location
	 */
	public Separator(FileLocation location) {
		super(location);
	}

}
