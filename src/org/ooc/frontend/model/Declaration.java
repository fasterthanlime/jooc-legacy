package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Declaration extends Expression {

	protected String name;
	protected String externName;
	
	public Declaration(String name, Token startToken) {
		this(name, null, startToken);
	}
	
	public Declaration(String name, String externName, Token startToken) {
		super(startToken);
		this.name = name;
		this.externName = externName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract TypeDecl getTypeDecl();
	
	public boolean isExtern() {
		return externName != null;
	}
	
	public String getExternName() {
		if(externName == null || externName.length() == 0) return getName();
		return externName;
	}
	
	public String getExternName(VariableAccess variableAccess) {
		if(externName == null || externName.length() == 0) return variableAccess.getName();
		return externName;
	}
	
	public void setExternName(String externName) {
		this.externName = externName;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + name;
	}
	
}
