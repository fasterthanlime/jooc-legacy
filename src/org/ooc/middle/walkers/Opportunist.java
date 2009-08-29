package org.ooc.middle.walkers;

import java.io.IOException;

import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;

/**
 * @return false if you want to stop, true if you wanna continue.
 */
public interface Opportunist<T> {
	public boolean take(T node, NodeList<Node> stack) throws IOException;
}