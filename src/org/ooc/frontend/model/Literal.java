package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Literal extends Expression {

	public Literal(Token startToken) {
		super(startToken);
	}

}
