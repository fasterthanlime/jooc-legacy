package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class FloatLiteral extends Literal {

	protected double value;
	public static Type type = new Type("Double", Token.defaultToken);

	public FloatLiteral(double value, Token startToken) {
		super(startToken);
		this.value = value;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public Type getType() {
		return type;
	}
	
	public double getValue() {
		return value;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {}

	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
