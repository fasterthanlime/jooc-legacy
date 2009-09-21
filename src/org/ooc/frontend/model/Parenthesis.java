package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Parenthesis extends Expression {

	protected Expression expression;

	public Parenthesis(Expression expression, Token startToken) {
		super(startToken);
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public Type getType() {
		return expression.getType();
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "Parenthesis: (" + expression + ")";
	}
	
}
