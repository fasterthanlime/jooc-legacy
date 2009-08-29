package org.ooc.nodes.others;

import org.ubi.FileLocation;

/**
 * A transparent block behaves exactly like a block, except that it's ignored
 * by some routines, e.g. when checking that this() or super() calls are the first
 * statement in a constructor.
 * 
 * @author Amos Wenger
 */
public class TransparentBlock extends Block {

	/**
	 * Default constructor
	 * @param location
	 */
	public TransparentBlock(FileLocation location) {
		super(location);
	}

}
