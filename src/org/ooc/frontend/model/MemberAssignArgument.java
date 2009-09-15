package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class MemberAssignArgument extends MemberArgument {

	public MemberAssignArgument(String name, Token startToken) {
		super(name, startToken);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	protected void doReplace(NodeList<Node> stack, VariableDecl decl,
			FunctionDecl funcDecl) {
		
		if(!funcDecl.getName().equals("new")) {
			funcDecl.getBody().add(0, new Line(new Assignment(
					new MemberAccess(name, startToken),
					new VariableAccess(name, startToken),
					startToken
			)));
		}
		
		stack.peek().replace(this, new RegularArgument(decl.getType(), name, startToken));
		
	}
	
}
