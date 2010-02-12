package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class NamespaceDecl extends Declaration {
	private NodeList<Import> imports;

	public NamespaceDecl(String name, Token startToken, Module module) {
		super(name, startToken, module);
		imports = new NodeList<Import>(startToken);
	}

	public void addImport(Import import_) {
		imports.add(import_);
	}

	public NodeList<Import> getImports() {
		return imports;
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		
	}

	public void accept(Visitor visitor) throws IOException {
	
	}

	public TypeDecl resolveType(String name) {
		for(Import imp: imports) {
			TypeDecl type = imp.getModule().getType(name);
			if(type != null) return type;
		}
		return null;
	}

	public Type getType() {
		return null;
	}

	public boolean hasChildren() {
		return !imports.isEmpty();
	}

	@Override
	public TypeDecl getTypeDecl() {
		return null;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
}
