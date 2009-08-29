package org.ooc.nodes.operators;

import org.ooc.nodes.others.RawCode;
import org.ubi.FileLocation;

/**
 * The assignment operator.
 *
 * @author Amos Wenger
 */
public class Assignment extends RawCode {

	/**
	 * Default constructor
	 * @param location
	 */
    public Assignment(FileLocation location) {
        super(location, "=");
    }

}
