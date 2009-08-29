package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.Parenthesis;
import org.ubi.FileLocation;

/**
 * Conditional jumping
 * 
 * @author Amos Wenger
 */
public class If extends Parenthesis {

	/**
	 * Default constructor
	 * @param location
	 */
	public If(FileLocation location) {
		super(location);
	}

	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
		
		writeWhitespace(a);
		a.append("if");
		super.writeToCSource(a);

	}
	
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		assembleAll(manager);
		
	}

}
