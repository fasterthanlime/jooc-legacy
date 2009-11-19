package org.ooc.frontend.model;

import java.io.IOException;
import java.math.BigInteger;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class IntLiteral extends Literal {

	public static enum Format {
		DEC,
		OCT,
		HEX,
		BIN,
	}
	
	protected BigInteger value;
	protected Format format;
	public static Type type = new Type("Int", Token.defaultToken);
	
	public IntLiteral(long value, Format format, Token startToken) {
		this(new BigInteger(String.valueOf(value)), format, startToken);
	}
	
	public IntLiteral(BigInteger value, Format format, Token startToken) {
		super(startToken);
		this.value = value;
		this.format = format;
	}
	
	public Type getType() {
		return type;
	}
	
	public BigInteger getValue() {
		return value;
	}
	
	public Format getFormat() {
		return format;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		visitor.visit(type);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
}
