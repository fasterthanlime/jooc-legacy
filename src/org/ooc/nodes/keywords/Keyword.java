package org.ooc.nodes.keywords;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * Base class for modifier keywords
 *
 * @author Amos Wenger
 */
public abstract class Keyword extends SyntaxNode {

	/**
	 * Create a new keyword
	 * @param location
	 */
	public Keyword(FileLocation location) {
		super(location);
	}
	
}
