package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class While extends Conditional {

	public While(Expression condition, Token startToken) {
		super(condition, startToken);
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == condition) {
			condition = (Expression) kiddo;
			return true;
		}
		return false;
		
	}

}
