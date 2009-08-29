package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class FloatLiteral extends Literal {

	protected double value;
	public static Type type = new Type("Float", Token.defaultToken);

	public FloatLiteral(double value, Token startToken) {
		super(startToken);
		this.value = value;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public double getValue() {
		return value;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}

	@Override
	public boolean hasChildren() {
		return false;
	}

}
