package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class FuncType extends Type {

	private FunctionDecl decl;
	
	private FuncType(Token startToken) {
		super("Func", startToken);
	}
	
	public FuncType(Token startToken, FunctionDecl decl) {
		this(startToken);
		this.decl = decl;
		for(TypeParam typeParam: decl.getTypeParams().values()) {
			typeParams.add(new VariableAccess(typeParam.getName(), typeParam.startToken));
		}
		setRef(decl);
	}
	
	public FuncType(Token startToken, NodeList<Access> typeParams) {
		this(startToken);
		decl = new FunctionDecl("<function pointer>", "", false, false, false, true, startToken, null);
		decl.setFromPointer(true);
		
		if(typeParams != null) {
			typeParams.addAll(typeParams);
			for(Access typeParam: typeParams) {
				String name = ((VariableAccess) typeParam).getName();
				decl.getTypeParams().put(name, new TypeParam(name, typeParam.startToken));
			}
		}
		
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
		FuncType copy =  new FuncType(startToken, typeParams);
		copy.decl = decl;
		return copy;
	}
	
	@Override
	public String toString() {
		return "FuncType|"+super.toString();
	}
}
