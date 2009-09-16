package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class MemberAssignArgument extends MemberArgument {

	public MemberAssignArgument(String name, Token startToken) {
		super(name, startToken);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public Response doReplace(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;
		
		if(ref == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Can't resolve member-assign-argument "+getName()+", no such field.");
			}
			return Response.LOOP;
		}
		
		int funcDeclIndex = stack.find(FunctionDecl.class);
		if(funcDeclIndex == -1) {
			throw new OocCompilationError(this, stack, "Member-assign argument outside a function definition? wtf.");
		}
		FunctionDecl funcDecl = (FunctionDecl) stack.get(funcDeclIndex);
		
		if(!funcDecl.getName().equals("new")) {
			funcDecl.getBody().add(0, new Line(new Assignment(
					new VariableAccess(ref, startToken),
					new VariableAccess(this, startToken),
					startToken
			)));
		}
		
		stack.peek().replace(this, new RegularArgument(ref.getType(), name, startToken));
		
		return Response.OK;
		
	}
	
}
