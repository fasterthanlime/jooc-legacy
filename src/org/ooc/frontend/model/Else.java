package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Else extends ControlStatement {

	public Else(Token startToken) {
		super(startToken);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		body.accept(visitor);
	}

	public boolean hasChildren() {
		return !body.isEmpty();
	}

}
