package org.ooc.middle;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.tokens.Token;
import org.ubi.CompilationFailedError;

public class OocCompilationError extends CompilationFailedError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1356486317872050599L;

	public OocCompilationError(Node node, NodeList<Node> stack, String message) {
		this(node.startToken, stack.getModule(), message);
	}
	
	public OocCompilationError(Node node, Module module, String message) {
		this(node.startToken, module, message);
	}
	
	public OocCompilationError(Token startToken, NodeList<Node> stack, String message) {
		this(startToken, stack.getModule(), message);
	}
	
	public OocCompilationError(Token startToken, Module module, String message) {
		super(module.getReader().getLocation(startToken), "[ERROR] " + message);
	}

	@Override
	public void printStackTrace() {
		System.err.println(toString());
	}
	
	@Override
	public String toString() {
		return getMessage().trim();
	}

}
