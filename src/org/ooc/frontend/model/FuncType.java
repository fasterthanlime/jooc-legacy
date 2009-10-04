package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class FuncType extends Type {

	private FunctionDecl decl;
	
	public FuncType(Token startToken) {
		super("Func", startToken);
		decl = new FunctionDecl("<function pointer>", "", false, false, false, true, startToken);
		decl.setFromPointer(true);
		setRef(decl);
	}

	public FunctionDecl getDecl() {
		return decl;
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		// hah nothing to worry about =)
		return Response.OK;
		
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
	
	@Override
	public Type clone() {
		System.out.println(" // cloned a FuncType!");
		FuncType copy =  new FuncType(startToken);
		copy.decl = decl;
		return copy;
	}
	
	@Override
	public String toString() {
		return "FuncPointer";
	}
}
