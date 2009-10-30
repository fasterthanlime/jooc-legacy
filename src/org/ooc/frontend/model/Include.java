package org.ooc.frontend.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.Versioned;
import org.ooc.frontend.model.tokens.Token;

public class Include extends Node implements Versioned {

	public static enum Mode {
		LOCAL,
		PATHY,
	}
	
	public static class Define {
		public String name;
		public String value;
	}
	
	protected String include;
	protected Mode mode;
	protected final List<Define> defines;
	private VersionBlock version;

	public Include(String include, Mode mode, Token startToken) {
		super(startToken);
		this.include = include;
		this.mode = mode;
		this.defines = new ArrayList<Define>();
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public String getPath() {
		return include;
	}
	
	public List<Define> getDefines() {
		return defines;
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
	
	@Override
	public String toString() {
		return super.toString()+" : "+include;
	}

	public void setVersion(VersionBlock version) {
		this.version = version;
	}

	public VersionBlock getVersion() {
		return version;
	}
	
}
