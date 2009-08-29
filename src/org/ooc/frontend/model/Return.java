package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Return extends Statement {
	
	public Return(Token startToken) {
		super(startToken);
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
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	

}
