package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The return of the Conditional jumping - II
 * 
 * @author Amos Wenger
 */
public class Else extends SyntaxNode {

	/**
	 * Default constructor
	 * @param location
	 */
	public Else(FileLocation location) {
		super(location);
	}

	
	public void writeToCSource(Appendable a) throws IOException {
		
		writeWhitespace(a);
		a.append("else");

	}

}
