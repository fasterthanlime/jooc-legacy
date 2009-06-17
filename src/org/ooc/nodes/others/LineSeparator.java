package org.ooc.nodes.others;

import java.io.IOException;

import org.ubi.FileLocation;

/**
 * The line separator in ooc, aka semi-colon ';'
 *
 * @author Amos Wenger
 */
public class LineSeparator extends Separator {

	/**
	 * Default constructor
	 * @param location
	 */
    public LineSeparator(FileLocation location) {
        super(location);
    }
    
    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	a.append(";");
    }

}
