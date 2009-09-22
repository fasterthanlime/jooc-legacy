package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

/**
 * condition ? valueIfTrue else valueIfFalse 
 * 
 * @author Amos Wenger
 */
public class Ternary extends Expression {

	public Ternary(Token startToken, Expression condition, Expression valueIfTrue, Expression valueIfFalse) {
		super(startToken);
		this.condition = condition;
		this.valueIfTrue = valueIfTrue;
		this.valueIfFalse = valueIfFalse;
	}

	private Expression condition;
	private Expression valueIfTrue;
	private Expression valueIfFalse;
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == condition) {
			condition = (Expression) kiddo;
			return true;
		}
		if(oldie == valueIfTrue) {
			valueIfTrue = (Expression) kiddo;
			return true;
		}
		if(oldie == valueIfFalse) {
			valueIfFalse = (Expression) kiddo;
			return true;
		}
		return false;
	}

	public Type getType() {
		return valueIfTrue.getType();
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		condition.accept(visitor);
		valueIfTrue.accept(visitor);
		valueIfFalse.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public Expression getValueIfTrue() {
		return valueIfTrue;
	}
	
	public Expression getValueIfFalse() {
		return valueIfFalse;
	}

}
