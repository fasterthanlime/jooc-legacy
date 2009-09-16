package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class MemberArgument extends Argument {
	
	protected VariableDecl ref = null;
	
	public MemberArgument(String name, Token startToken) {
		super(null, name, startToken);
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
	public Type getType() {
		return ref == null ? null : ref.getType();
	}
	
	@Override
	public boolean isResolved() {
		return false;
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;

		if(ref == null) {
			int typeDeclIndex = stack.find(TypeDecl.class);
			if(typeDeclIndex == -1) {
				throw new OocCompilationError(this, stack, "Couldn't resolve "+getClass().getSimpleName()+" "+name
						+" (not even in a TypeDecl! who d'you think you're kidding?)");
			}
			TypeDecl typeDecl = (TypeDecl) stack.get(typeDeclIndex);
			ref = typeDecl.getVariable(getName());
		}
		
		if(ref == null && fatal) {
			throw new OocCompilationError(this, stack, getClass().getSimpleName()
					+" named '"+name+"" +
					"' doesn't correspond to any real member variable.");
		}
		
		if(getType() == null) {
			if(fatal) throw new OocCompilationError(type, stack, "Couldn't resolve "
					+getClass().getSimpleName()+" "+name);
			
			return Response.LOOP;
		}
		
		int funcIndex = stack.find(FunctionDecl.class);
		if(funcIndex == -1) {
			throw new OocCompilationError(this, stack, getClass().getSimpleName()
					+" outside a function definition? What have" +
					" you been up to?");
		}		
		
		return doReplace(stack, res, fatal);
		
	}
	
	@Override
	public boolean unwrap(NodeList<Node> stack) throws OocCompilationError, EOFException {
		return false;
	}

	protected Response doReplace(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		stack.peek().replace(this, new RegularArgument(ref.getType(), name, startToken));
		return Response.OK;
		
	}
	
}
