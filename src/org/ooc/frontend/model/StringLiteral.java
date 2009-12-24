package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class StringLiteral extends Literal {

	protected String value;
	public static Type type = new Type("String", Token.defaultToken);
	
	public StringLiteral(String value, Token startToken) {
		super(startToken);
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	@Override
	public String toString() {
		return "\""+value+"\"";
	}

}
