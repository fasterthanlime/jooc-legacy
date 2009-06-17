package org.ooc.nodes.operators;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.RawCode;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The '*' character. Often assembled into a Mul.
 * 
 * @author Amos Wenger
 */
public class Star extends RawCode {

	/**
	 * Default constructor
	 * @param location
	 */
	public Star(FileLocation location) {
		
		super(location, " * ");
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		//TODO add precedence of operators
		SyntaxNode prev = getPrev();
		SyntaxNode next = getNext();
		if(prev != null && next != null) {
			if(!manager.isDirty(prev) && !manager.isDirty(next)) {
				replaceWith(manager, new Mul(location, prev, next));
			}
		}
		
	}
	
}
