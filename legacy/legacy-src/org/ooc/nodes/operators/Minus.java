package org.ooc.nodes.operators;

import org.ooc.nodes.others.RawCode;
import org.ubi.FileLocation;

/**
 * The minus symbol, either to negate a number of subtract two numbers.
 *
 * @author Amos Wenger
 */
public class Minus extends RawCode {

	/**
	 * Default constructor
	 * @param location
	 */
    public Minus(FileLocation location) {
        super(location, " -");
    }

}
