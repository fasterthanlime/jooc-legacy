package org.ooc.nodes.interfaces;

import org.ooc.nodes.types.Type;

/**
 * Implemented by anything which has a type (e.g. int), such as literals,
 * various expressions (e.g. addition, multiplication), parenthesis, function
 * calls, etc.
 *
 * @author Amos Wenger
 */
public interface Typed {

	/**
	 * @return the type of this object
	 */
    public Type getType();

}
