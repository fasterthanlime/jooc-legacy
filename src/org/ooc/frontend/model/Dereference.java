package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Dereference extends Access {

	Type type;
	Expression expression;
	
	public Dereference(Expression expression, Token startToken) {
		super(startToken);
		setExpression(expression);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(expression == oldie) {
			expression = (Expression) kiddo;
			return true;
		}
		
		return false;
	}

	@Override
	public Type getType() {
		if(type == null) {
			Type exprType = expression.getType();
			if(exprType != null) {
				Declaration ref = exprType.getRef();
				if(ref instanceof CoverDecl) {
					Type fromType = ((CoverDecl) ref).getFromType();
					if(fromType != null) exprType = fromType;
				}
				type = new Type(exprType.getName(), exprType.getPointerLevel() - 1, exprType.startToken);
				type.setRef(exprType.getRef());
			}
		}
		return type;
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
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

}
