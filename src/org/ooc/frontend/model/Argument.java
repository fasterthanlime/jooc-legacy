package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Argument extends VariableDecl {

	public Argument(Type type, String name, Token startToken) {
		super(type, false, startToken, null);
		VariableDeclAtom vda = new VariableDeclAtom(name, null, startToken);
		getAtoms().add(vda);
		this.name = name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		getAtoms().get(0).name = name;
	}
	
	@Override
	public String getName() {
		return getAtoms().get(0).name = name;
	}
	
}
