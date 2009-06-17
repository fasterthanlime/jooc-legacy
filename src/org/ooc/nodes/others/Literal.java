package org.ooc.nodes.others;

import org.ooc.nodes.interfaces.Typed;
import org.ubi.FileLocation;

/**
 * Base class for Integer literals, String literal, Null literals, etc.
 *
 * @author Amos Wenger
 */
public abstract class Literal extends SyntaxNode implements Typed {

	/**
	 * Default constructor
	 * @param location
	 */
    public Literal(FileLocation location) {
        super(location);
    }
    
    @Override
    public String getDescription() {
    	return toString();
    }
    
}
