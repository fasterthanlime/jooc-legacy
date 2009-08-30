package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class FuncType extends Type {

	private FunctionDecl decl;
	
	public FuncType(Token startToken) {
		super("Func", startToken);
		decl = new FunctionDecl("<function pointer>", "", false, false, false, true, startToken);
		decl.setFromPointer(true);
		ref = decl;
	}

	public FunctionDecl getDecl() {
		return decl;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		super.acceptChildren(visitor);
		decl.accept(visitor);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
}
