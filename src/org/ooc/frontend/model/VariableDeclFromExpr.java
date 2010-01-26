package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class VariableDeclFromExpr extends VariableDecl {

	protected boolean isConst;
	
	public VariableDeclFromExpr(String name, Expression expression, Token startToken, Module module) {
		this(name, expression, false, startToken, module);
	}

	public VariableDeclFromExpr(String name, Expression expression, boolean isStatic, Token startToken, Module module) {
		super(null, isStatic, startToken, module);
		atoms.add(new VariableDeclAtom(name, expression, startToken));
	}
	
	public boolean isConst() {
		return isConst;
	}
	
	public void setConst(boolean isConst) {
		this.isConst = isConst;
	}

	@Override
	public Type getType() {
		if(type != null) {
			return type;
		}
		VariableDeclAtom atom = atoms.get(0);
		Expression expr = atom.getExpression();
		if(expr == null) {
			if(atom.assign != null) {
				Type retType = atom.assign.getRight().getType();
				if(isConst && retType != null) {
					retType = retType.clone();
					retType.setConst(true);
				}
				return retType;
			}
			return type;
		}
		Type retType = expr.getType();
		if(isConst && retType != null) {
			retType = retType.clone();
			retType.setConst(true);
		}
		return retType == null ? type : retType;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		atoms.accept(visitor);
		Type type = getType();
		if(type != null && !type.getName().equals("This")) {
			type.accept(visitor);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" "+getName()+":"+getType()+" = "+atoms.getFirst().getExpression();
	}
	
	@Override
	public void unwrapToClassInitializers(NodeList<Node> hierarchy,
			ClassDecl classDecl) {
		super.unwrapToClassInitializers(hierarchy, classDecl);
		atoms.get(0).setExpression(null);
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		VariableDeclAtom atom = atoms.get(0);
		Expression expr = atom.getExpression();
		if(expr != null && expr instanceof MustBeResolved) {
			((MustBeResolved) expr).resolve(stack, res, false);
		}
		
		if(expr != null && expr.getType() == null) {
			if(fatal) {
				throw new OocCompilationError(expr, stack, "Couldn't resolve "+expr);
			}
			return Response.LOOP;
		}
		if(expr != null && expr.getType().isGenericRecursive() && expr.getType().isFlat()) {
			addAfterLine(stack, toDeclAssign(stack, atom, expr)); 
			return Response.RESTART;
		}
		
		if(expr != null && stack.peek(3) instanceof Module) {
			((Module) stack.peek(3)).getLoadFunc().getBody().add(0, new Line(toDeclAssign(stack, atom, expr)));
		}
		
		return super.resolve(stack, res, fatal);
	}

	private Assignment toDeclAssign(NodeList<Node> stack, VariableDeclAtom atom, Expression expr) {
		
		VariableDecl decl = new VariableDecl(expr.getType(), false, startToken, module);
		decl.setGlobal(isGlobal());
		decl.getAtoms().add(new VariableDeclAtom(atom.getName(), null, startToken));
		
		VariableAccess acc = new VariableAccess(decl, startToken);
		Assignment ass = new Assignment(acc, expr, startToken);
		
		stack.peek().replace(this, decl);
		return ass;
		
	}

	@Override
	public boolean shouldBeLowerCase() {
		if(!super.shouldBeLowerCase()) return false;
		if(isConst) return false;
		return true;
	}
	
}
