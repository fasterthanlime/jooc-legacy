package org.ooc.frontend.model;

import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public abstract class Literal extends Expression implements MustBeResolved {

	public Literal(Token startToken) {
		super(startToken);
	}
	
	public boolean isResolved() {
		return getType() != null && getType().isResolved();
	}
	
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		if(getType() != null) {
			getType().resolve(stack, res, fatal);
		}
		
		return isResolved() ? Response.OK : Response.LOOP; 
	}

}
