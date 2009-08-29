package org.ooc.middle;

import java.io.EOFException;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ubi.CompilationFailedError;

public class OocCompilationError extends CompilationFailedError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1356486317872050599L;

	public OocCompilationError(Node node, NodeList<Node> stack, String message) throws EOFException {
		super(((Module) stack.get(0)).getReader().getLocation(node.startToken), "[ERROR] " + message);
	}
	
	public OocCompilationError(Node node, Module module, String message) throws EOFException {
		super(module.getReader().getLocation(node.startToken), "[ERROR] " + message);
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
