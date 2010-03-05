package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class CommaSequence extends Expression {

	NodeList<Statement> body;
	
	public Type getType() {
		return body.isEmpty() ? null : ((Expression) body.getLast()).getType();
	}
	
	public CommaSequence(Token startToken) {
		super(startToken);
		body = new NodeList<Statement>();
	}
	
	public NodeList<Statement> getBody() {
		return body;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return body.replace(oldie, kiddo);
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		for (Statement stmt : body) {
			stmt.accept(visitor);
		}
	}

	public boolean hasChildren() {
		return !body.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		boolean isFirst = true;
		for(Statement stmt : body) {
			if(isFirst) isFirst = false;
			else        sb.append(", ");
			sb.append(stmt.toString());
		}
		sb.append(')');
		return sb.toString();
	}

}
