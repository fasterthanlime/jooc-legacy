package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class ValuedReturn extends Return {

	protected Expression expression;

	public ValuedReturn(Expression expression, Token startToken) {
		super(startToken);
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(super.replace(oldie, kiddo)) return true;
		
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		
		return super.replace(oldie, kiddo);
		
	}
	
}
