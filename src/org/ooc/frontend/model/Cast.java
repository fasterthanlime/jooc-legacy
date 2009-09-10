package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ubi.CompilationFailedError;

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

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
		expression.accept(visitor);
	}

	@Override
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
		if(type.isGeneric() && expression.getType() != null && expression.getType().isGeneric()) {
			type = type.clone();
			TypeDecl dstDecl = (TypeDecl) newType.getRef();
			Type src = expression.getType();
			if(dstDecl.getGenericTypes().size() != src.getGenericTypes().size()) {
				throw new CompilationFailedError(null, "Invalid cast between types "+dstDecl.getType()+" and "+src);
			}
			type.getGenericTypes().clear();
			type.getGenericTypes().addAll(src.getGenericTypes());
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
