package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class VarArg extends Argument {

	public VarArg(Token startToken) {
		this(false, startToken);
	}
	
	public VarArg(boolean isConst, Token startToken) {
		// TODO add special trickery to properly handle "any type"
		super(new Type("", startToken), "...", isConst, startToken);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
