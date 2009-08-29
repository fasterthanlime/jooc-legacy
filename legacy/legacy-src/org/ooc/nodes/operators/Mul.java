package org.ooc.nodes.operators;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The multiplication operation, e.g. 3 * 5
 * 
 * @author Amos Wenger
 */
public class Mul extends MathOp {

	/**
	 * Default constructor
	 * @param location
	 * @param prev
	 * @param next
	 */
	public Mul(FileLocation location, SyntaxNode prev, SyntaxNode next) {
		super(location, prev, next, "* ");
	}

}
