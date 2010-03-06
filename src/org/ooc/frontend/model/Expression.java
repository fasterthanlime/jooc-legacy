package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Expression extends Statement implements Typed {

	public Expression(Token startToken) {
		super(startToken);
	}

	public Expression getInner() {
		return this;
	}
	
	public Expression getGenericOperand() {
		return getType().isGeneric() ? this : new AddressOf(this, startToken);
	}

}
