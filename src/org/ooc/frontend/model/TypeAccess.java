package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;


public class TypeAccess extends VariableAccess {

	private Type type;

	public TypeAccess(Type type) {
		super((String) null, type.startToken);
		this.type = type;
	}
	
	@Override
	public String getName() {
		return type.getName();
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public Declaration getRef() {
		return type.getRef();
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(type.isResolved()) return Response.OK;
		
		if(type.isFlat() && type.getTypeParams().isEmpty()) {
			VariableDecl var = getVariable(type.getName(), stack);
			if(var != null && var.getType() != null && var.getType().getName().equals("Class")) {
				type.setRef(var);
			}
		}
		
		if(fatal) throw new OocCompilationError(this, stack, "Can't resolve type access to "+type);
		return Response.LOOP; 
		
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
}


