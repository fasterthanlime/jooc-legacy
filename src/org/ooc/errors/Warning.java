package org.ooc.errors;

import org.ooc.nodes.others.SyntaxNode;

/**
 * Contains information about a Warning message issued by the compiler on a
 * certain node.
 * 
 * @author Amos Wenger
 */
public class Warning {

	/**
	 * Details about the warnings, e.g. recommendations, why it could
	 * screw up, etc.
	 */
	public final String message;

	/** 
	 * The node on which the warning has been issued. Can be used by
	 * development tools to show where is the error.
	 */
	public final SyntaxNode node;
	
	/**
	 * Default constructor.
	 * @param message
	 * @param node
	 */
	public Warning(String message, SyntaxNode node) {
		
		this.message = message;
		this.node = node;
		
	}
	
}
