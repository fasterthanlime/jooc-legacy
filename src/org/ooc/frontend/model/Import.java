package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Import extends Node {

	protected String name;
	protected Module module;

	public Import(String name, Token defaultToken) {
		super(defaultToken);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Module getModule() {
		return module;
	}
	
	public void setModule(Module module) {
		this.module = module;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public String getPath() {
		return name.replace('.', File.separatorChar) + ".ooc";
	}
	
	@Override
	public String toString() {
		return super.toString()+" : "+name;
	}
	
}
