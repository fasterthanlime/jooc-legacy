package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class VariableDeclFromExpr extends VariableDecl implements MustBeResolved {

	protected boolean isConst;
	
	public VariableDeclFromExpr(String name, Expression expression, Token startToken) {
		this(name, expression, false, startToken);
	}
	
	public VariableDeclFromExpr(String name, Expression expression, boolean isStatic, Token startToken) {
		super(null, isStatic, startToken);
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
		VariableDeclAtom atom = atoms.get(0);
		Expression expr = atom.getExpression();
		if(expr == null) {
			if(atom.assign != null) {
				Type retType = atom.assign.getRight().getType();
				if(isConst && retType != null) {
					retType = retType.copy();
					retType.setConst(true);
				}
				return retType;
			}
			return null;
		}
		Type retType = expr.getType();
		if(isConst && retType != null) {
			retType = retType.copy();
			retType.setConst(true);
		}
		return retType;
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
		return getClass().getSimpleName()+" "+getName()+":"+getType();
	}
	
	@Override
	protected void unwrapToClassInitializers(NodeList<Node> hierarchy,
			ClassDecl classDecl) {
		super.unwrapToClassInitializers(hierarchy, classDecl);
		atoms.get(0).setExpression(null);
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		VariableDeclAtom atom = atoms.get(0);
		Expression expr = atom.getExpression();
		if(expr != null && expr instanceof MustBeResolved) {
			((MustBeResolved) expr).resolve(stack, res, false);
		}
		
		return false;
	}
	
}
