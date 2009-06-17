package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The infamous, horrible, fearless C goto. 
 * 
 * @author Amos Wenger
 */
public class Goto extends SyntaxNode {

	private final String label;

	/**
	 * Default constructor for a goto to 'label'
	 * @param location
	 * @param label the name of the label we're supposed to go to.
	 */
	public Goto(FileLocation location, String label) {

		super(location);
		this.label = label.trim();
		
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {

		writeWhitespace(a);
		a.append("goto ");
		a.append(label);

	}
	
	@Override
	protected boolean isSpaced() {
		
		return true;
		
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

}
