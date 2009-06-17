package org.ooc.nodes.operators;

import org.ooc.nodes.others.RawCode;
import org.ubi.FileLocation;

/**
 * A dot, ie "."
 * Mostly used to call member functions or to access a structure's members
 * 
 * @author Amos Wenger
 */
public class Dot extends RawCode {

	/**
	 * Default constructor
	 * @param location
	 */
	public Dot(FileLocation location) {
        super(location, ".");
    }
    
    @Override
    protected boolean isSpaced() {
	    return false;
    }

}
