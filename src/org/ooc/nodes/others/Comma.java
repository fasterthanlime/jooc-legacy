package org.ooc.nodes.others;

import java.io.IOException;

import org.ubi.FileLocation;

/**
 * A comma, e.g. ","
 * Separates arguments in function declaration/call, for example.
 *
 * @author Amos Wenger
 */
public class Comma extends Separator {

	/**
	 * Default constructor
	 * @param location
	 */
    public Comma(FileLocation location) {
        super(location);
    }
    
    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	a.append(",");
    }

}
