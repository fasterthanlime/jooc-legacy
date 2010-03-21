package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Argument extends VariableDecl {

	public Argument(Type type, String name, Token startToken) {
		super(type, name, startToken, null);
	}
	
	@Override
	public boolean isArg() {
		return true;
	}
	
}
