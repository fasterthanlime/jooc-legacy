package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class CharLiteral extends Literal {

	protected char value;
	public static Type type = new Type("Char", Token.defaultToken);
	
	public CharLiteral(char value, Token startToken) {
		super(startToken);
		this.value = value;
	}
	
	public char getValue() {
		return value;
	}
	
	public void setValue(char value) {
		this.value = value;
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
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor); 
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

}
