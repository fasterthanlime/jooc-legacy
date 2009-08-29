package org.ooc.nodes.operators;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;


/**
 * The '+' character. Often assembled into a Add.
 * 
 * @author Amos Wenger
 */
public class Add extends MathOp {

	/**
	 * Default constructor
	 * @param location
	 * @param prev
	 * @param next
	 */
	public Add(FileLocation location, SyntaxNode prev, SyntaxNode next) {
		super(location, prev, next, "+ ");
	}
	
}
