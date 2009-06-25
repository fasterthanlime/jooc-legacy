package org.ooc.nodes.preprocessor;

import java.io.IOException;

import org.ooc.nodes.others.RawCode;
import org.ubi.FileLocation;

/**
 * Raw piece of code that should be written to a C header rather than to a
 * C source.
 * 
 * @author Amos Wenger
 */
public class RawHeaderCode extends RawCode {

	/**
	 * Default constructor
	 * @param location
	 * @param content
	 */
	public RawHeaderCode(FileLocation location, String content) {
		super(location, content);
	}

	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
		// Tadelitedalouu..
	}
	
	
	@Override
	public void writeToCHeader(Appendable a) throws IOException {
		a.append(content);
	}
	
}
