package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.UseDef;

public class Use extends Node {

	protected String identifier;
	protected UseDef useDef;
	
	public Use(UseDef useDef, Token startToken) {
		super(startToken);
		this.useDef = useDef;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String name) {
		this.identifier = name;
	}
	
	public UseDef getUseDef() {
		return useDef;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {}

	public boolean hasChildren() {
		return false;
	}

	public boolean isResolved() {
		return useDef != null;
	}

}
