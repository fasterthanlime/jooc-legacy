package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class TypeParam extends TypeDecl {

	private Type type;
	private Argument argument;
	private boolean isGhost = false;
	
	public TypeParam(String name, Token startToken) {
		super(name, null, null, startToken);
		type = new Type("Class", Token.defaultToken);
		argument = new RegularArgument(type, name, startToken);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public Argument getArgument() {
		return argument;
	}

	public void accept(Visitor visitor) throws IOException {}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		type.acceptChildren(visitor);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}
	
	public boolean isGhost() {
		return isGhost;
	}
	
	public void setGhost(boolean isGhost) {
		this.isGhost = isGhost;
	}
	
}
