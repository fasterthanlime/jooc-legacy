package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Cast extends Expression {

	protected Expression expression;
	protected Type type;
	
	public Cast(Expression expression, Type targetType, Token startToken) {
		super(startToken);
		this.expression = expression;
		setType(targetType);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	public Type getType() {
		return type;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
		expression.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public void setType(Type newType) {
		this.type = newType;
		if(type.isGenericRecursive() && expression.getType() != null && expression.getType().isGenericRecursive()) {
			type = type.clone();
			TypeDecl dstDecl = (TypeDecl) newType.getRef();
			Type src = expression.getType();
			if(dstDecl.getTypeParams().size() != src.getTypeParams().size()) {
				throw new Error("Invalid cast between types "+dstDecl.getType()+" and "+src);
			}
			type.getTypeParams().clear();
			type.getTypeParams().addAll(src.getTypeParams());
		}
	}
	
	@Override
	public Expression getInner() {
		return getExpression();
	}
	
	@Override
	public String toString() {
		return "["+expression+" as "+type+"]";
	}

}
