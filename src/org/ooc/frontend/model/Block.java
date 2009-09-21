package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Block extends Statement {

	protected final NodeList<Line> body;
	
	public Block(Token startToken) {
		super(startToken);
		body = new NodeList<Line>(startToken);
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		body.accept(visitor);
	}

	public boolean hasChildren() {
		return body.hasChildren();
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	public NodeList<Line> getBody() {
		return body;
	}

}
