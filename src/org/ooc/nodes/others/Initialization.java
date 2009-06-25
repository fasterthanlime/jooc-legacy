package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ubi.FileLocation;

/**
 * Initialization of a variable at the start-up of the program, e.g.
 * <code>
 * String[] procedure = {"Ready?", "Steady?", "Pants off!"}; // declaration is initialization ;)
 * </code>
 * 
 * It's effectively a blend of an assignment and and a value node, mainly so that
 * it's recognized by a {@link ClassDef} and written to source, for static members.
 * 
 * @author Amos Wenger
 */
public class Initialization extends SyntaxNode {

	private SyntaxNode value;

	/**
	 * Default constructor to a specified value
	 * @param location
	 * @param value
	 */
	public Initialization(FileLocation location, SyntaxNode value) {
		super(location);		
		this.value = value;
	}

	
	public void writeToCSource(Appendable a) throws IOException {
		
		// Tralala...

	}
	
	
	@Override
	protected boolean isSpaced() {
	
		return true;
		
	}

	/**
	 * The value something is initialized to
	 * @return
	 */
	public SyntaxNode getValue() {
		
		return value;

	}
	
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		value.setContext(getParent());
		manager.queue(value, "Initialization queuing it's value.");
		
	}

}
