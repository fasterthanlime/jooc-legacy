package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;

public class TypeArgument extends Argument {

	public TypeArgument(Type type, Token startToken) {
		this(type, false, startToken);
	}
	
	public TypeArgument(Type type, boolean isConst, Token startToken) {
		// TODO check if empty name isn't a problem
		super(type, "", isConst, startToken);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
