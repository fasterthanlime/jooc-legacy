package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class RangeLiteral extends Literal {

	protected Expression lower;
	protected Expression upper;
	protected static Type type = new Type("Range", Token.defaultToken);
	
	public RangeLiteral(Expression lower, Expression upper, Token startToken) {
		super(startToken);
		this.lower = lower;
		this.upper = upper;
	}
	
	public Expression getLower() {
		return lower;
	}
	
	public void setLower(Expression lower) {
		this.lower = lower;
	}
	
	public Expression getUpper() {
		return upper;
	}
	
	public void setUpper(Expression upper) {
		this.upper = upper;
	}

	public Type getType() {
		return type;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		lower.accept(visitor);
		upper.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == lower) {
			lower = (Expression) kiddo;
			return true;
		}
		
		if(oldie == upper) {
			upper = (Expression) kiddo;
			return true;
		}
		
		return false;
		
	}

}
