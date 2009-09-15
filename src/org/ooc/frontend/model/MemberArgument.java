package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class MemberArgument extends Argument {

	private VariableAccess expression;

	public MemberArgument(String name, Token startToken) {
		super(new Type("", startToken), name, startToken);
		expression = new MemberAccess(name, startToken);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
	}
	
	@Override
	public Type getType() {
		return expression.getType();
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;
		
		if(expression.getType() == null) {
			System.out.println("Null exprType, looping... expr is "+expression+" expr ref is "+expression.getRef());
			if(fatal) throw new OocCompilationError(expression, stack, "Couldn't resolve "
					+getClass().getSimpleName()+" "+name);
			
			return Response.LOOP;
		}
		doUnwrap(stack);
		
		return Response.OK;
		
	}
	
	@Override
	public boolean unwrap(NodeList<Node> stack) throws OocCompilationError, EOFException {
		return false;
	}
	
	public boolean doUnwrap(NodeList<Node> stack) throws OocCompilationError {
		
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
