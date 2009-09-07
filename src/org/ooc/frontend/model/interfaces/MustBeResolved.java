package org.ooc.frontend.model.interfaces;

import java.io.IOException;

import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.middle.hobgoblins.Resolver;

public interface MustBeResolved {

	public static enum Response {
		OK, /** means resolve() doesn't need to be called ever again */
		RESTART, /** means the AST has been changed significantly and the Resolved must start over again */
		LOOP, /** means resolve() is counting on other nodes to resolve before trying again */
	}
	
	/**
	 * @return true if @link {@link Resolver} should do one more run, false otherwise.
	 */
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException;
	public boolean isResolved();
	
}
