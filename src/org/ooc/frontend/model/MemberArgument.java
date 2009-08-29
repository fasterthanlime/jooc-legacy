package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;

public class MemberArgument extends Argument {

	public MemberArgument(String name, Token startToken) {
		this(name, false, startToken);
	}
	
	public MemberArgument(String name, boolean isConst, Token startToken) {
		super(new Type("", startToken), name, isConst, startToken);
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
	public boolean unwrap(NodeList<Node> stack) throws OocCompilationError, EOFException {
		
		int typeIndex = stack.find(TypeDecl.class);
		if(typeIndex == -1) {
			throw new OocCompilationError(this, stack, getClass().getSimpleName()
					+" outside a class definition!");
		}
		
		TypeDecl typeDecl = (TypeDecl) stack.get(typeIndex);
		VariableDecl varDecl = typeDecl.getVariable(name);
		if(varDecl == null) {
			throw new OocCompilationError(this, stack, getClass().getSimpleName()
					+" named '"+name+"" +
					"' doesn't correspond to any real member variable.");
		}
		
		
		int funcIndex = stack.find(FunctionDecl.class);
		if(funcIndex == -1) {
			throw new OocCompilationError(this, stack, getClass().getSimpleName()
					+" outside a function definition? What have" +
					" you been up to?");
		}		
		FunctionDecl funcDecl = (FunctionDecl) stack.get(funcIndex);

		doReplace(stack, varDecl, funcDecl);
		return false;
		
	}

	protected void doReplace(NodeList<Node> stack, VariableDecl decl, FunctionDecl funcDecl) {
		stack.peek().replace(this, new RegularArgument(decl.getType(), name, startToken));
	}
	
}
