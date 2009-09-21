package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class InterfaceDecl extends TypeDecl {

	public InterfaceDecl(String name, Module module, Token startToken) {
		super(name, null, module, startToken);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

}
