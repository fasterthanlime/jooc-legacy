package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;


public class BoolLiteral extends Literal {

	public static Type type = new Type("Bool", Token.defaultToken);
	protected boolean value;
	
	public BoolLiteral(boolean value, Token token) {
		super(token);
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}

	public Type getType() {
		return type;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return false;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

}
