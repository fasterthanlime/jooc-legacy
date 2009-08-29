package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class RegularArgument extends Argument {

	public RegularArgument(Type type, String name, Token startToken) {
		this(type, name, false, startToken);
	}
	
	public RegularArgument(Type type, String name, boolean isConst, Token startToken) {
		super(type, name, isConst, startToken);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
}
