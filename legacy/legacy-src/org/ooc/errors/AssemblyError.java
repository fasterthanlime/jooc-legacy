package org.ooc.errors;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.SyntaxError;

/**
 * An error that has occured during the assembly (tree manipulation) phase.
 * Not necessarily fatal.
 * @see CompilationFailedError
 * 
 * @author Amos Wenger
 */
public class AssemblyError extends SyntaxError {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 3859238898523263251L;

	/** The problematic node */
	public final SyntaxNode node;
	
	/**
	 * Create a new error with a message on a specific node
	 * @param message
	 * @param node
	 */
	public AssemblyError(String message, SyntaxNode node) {
		super(node.location, message);
		this.node = node;
	}
	
}
