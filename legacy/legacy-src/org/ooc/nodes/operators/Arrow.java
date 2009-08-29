package org.ooc.nodes.operators;

import org.ooc.nodes.others.RawCode;
import org.ubi.FileLocation;

/**
 * An arrow, e.g. "->"  mostly used for accessing member to a structure from
 * a pointer to it.
 * For objects, though, the dot is used.
 * @see Dot
 *
 * @author Amos Wenger
 */
public class Arrow extends RawCode {

	/**
	 * Default constructor
	 * @param location
	 */
    public Arrow(FileLocation location) {
        super(location, "->");
    }
    
    
    @Override
	protected boolean isSpaced() {
	    return false;
    }
    
}
