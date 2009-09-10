package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class BuiltinType extends TypeDecl {

	protected Type type;
	
	public BuiltinType(String name) {
		this(name, Token.defaultToken);
	}
	
	public BuiltinType(String name, Token startToken) {
		super(name, null, startToken);
		type = new Type(name, startToken);
		type.setRef(this);
	}

	public BuiltinType(Type fromType) {
		this(fromType.getName());
	}

	@Override
	public Type getType() {
		return type ;
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
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
}
