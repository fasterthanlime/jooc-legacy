package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class Cast extends Expression implements MustBeResolved {

	protected Expression expression;
	protected Type type;
	
	public Cast(Expression expression, Type targetType, Token startToken) {
		super(startToken);
		this.expression = expression;
		setType(targetType);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	public Type getType() {
		return type;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
		expression.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public void setType(Type newType) {
		this.type = newType;
		if(type.isGenericRecursive() && expression.getType() != null && expression.getType().isGenericRecursive()) {
			type = type.clone();
			TypeDecl dstDecl = (TypeDecl) newType.getRef();
			Type src = expression.getType();
			if(dstDecl.getTypeParams().size() != src.getTypeParams().size()) {
				throw new Error("Invalid cast between types "+dstDecl.getType()+" and "+src);
			}
			type.getTypeParams().clear();
			type.getTypeParams().addAll(src.getTypeParams());
		}
	}
	
	@Override
	public Expression getInner() {
		return getExpression();
	}
	
	@Override
	public String toString() {
		return "["+expression+" as "+type+"]";
	}
	
	@Override
	public boolean canBeReferenced() {
		return expression.canBeReferenced();
	}

	public boolean isResolved() {
		return false;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		for(OpDecl op: res.module.getOps()) {
			if(tryOp(stack, op, res)) return Response.RESTART;
		}
		for(Import imp: res.module.getImports()) {
			for(OpDecl op: imp.getModule().getOps()) {
				if(tryOp(stack, op, res)) return Response.RESTART;
			}
		}		
		
		return Response.OK;
		
	}

	private boolean tryOp(NodeList<Node> stack, OpDecl op, Resolver res) {
		
		if(op.opType != OpType.AS) return false;
		
		FunctionDecl func = op.getFunc();
		NodeList<Argument> args = func.getArguments();
		
		if(expression.getType().softEquals(args.get(0).getType(), res) && type.softEquals(func.getReturnType(), res)) {
			System.out.println("Found potential match for "+this+" with op "+op);
			FunctionCall call = new FunctionCall(op.getFunc(), startToken);
			call.getArguments().add(expression);
			Node parent = stack.peek();
			parent.replace(this, call);
			call.resolve(stack, res, true);
		}
		
		return false;
		
	}

}
