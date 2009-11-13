package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;

public abstract class UnaryOperation extends Expression {

	Expression inner;
	
	public UnaryOperation(Expression inner, Token startToken) {
		super(startToken);
		this.inner = inner;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == inner) {
			inner = (Expression) kiddo;
			return true;
		}
		
		return false;
	}

	public Type getType() {
		return inner.getType();
	}
	
	@Override
	public Expression getInner() {
		return inner;
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		inner.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}
	
	public abstract OpType getOpType();

}
