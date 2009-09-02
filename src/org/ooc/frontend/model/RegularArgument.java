package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class RegularArgument extends Argument {

	public RegularArgument(Type type, String name, Token startToken) {
		super(type, name, startToken);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
