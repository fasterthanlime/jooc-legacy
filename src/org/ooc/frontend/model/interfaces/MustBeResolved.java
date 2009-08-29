package org.ooc.frontend.model.interfaces;

import java.io.IOException;

import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.middle.hobgoblins.Resolver;

public interface MustBeResolved {

	/**
	 * @return true if @link {@link Resolver} should do one more run, false otherwise.
	 */
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException;
	public boolean isResolved();
	
}
