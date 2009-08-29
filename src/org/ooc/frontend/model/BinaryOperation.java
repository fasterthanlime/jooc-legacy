package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

/**
 * Binary in the sense that it has a left and a right operand (e.g. binary op,
 * as opposed to unary op or ternary op)
 */
public abstract class BinaryOperation extends Expression implements MustBeResolved {

	protected Expression left;
	protected Expression right;
	
	public BinaryOperation(Expression left, Expression right, Token startToken) {
		super(startToken);
		this.left = left;
		this.right = right;
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public void setLeft(Expression left) {
		this.left = left;
	}
	
	public Expression getRight() {
		return right;
	}
	
	public void setRight(Expression right) {
		this.right = right;
	}
	
	@Override
	public Type getType() {
		// FIXME probably not right (haha)
		return getLeft().getType();
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		left.accept(visitor);
		right.accept(visitor);
	}
	
	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		if(left.getType() == null || right.getType() == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Can't resolve type of left "+
						left+" or right "+right+" operand. Wtf?");
			}
			return true;
		}
		
		OpType opType = getOpType();		
		for(OpDecl op: res.module.getOps()) {
			if(tryOp(stack, opType, op)) return false;
		}
		for(Import imp: res.module.getImports()) {
			for(OpDecl op: imp.getModule().getOps()) {
				if(tryOp(stack, opType, op)) return false;
			}
		}
		
		/*
		if(fopType.isNumeric() && left.getType().getClassification() == Classification.CLASS) {
			System.err.println(new OocCompilationError(this, stack, "Using operator "+opType.toPrettyString()+" between non-numeric types."
					+" Maybe you want to overload it? Do it like this: operator "
					+opType.toPrettyString()+" (left: "+left.getType()+", right: "+right.getType()+") { ... }").toString());
		}
		*/
		return false;
		
	}

	private boolean tryOp(NodeList<Node> stack, OpType opType, OpDecl op)
			throws OocCompilationError, EOFException {
		boolean end = false;
		if(op.getOpType() == opType) {
			if(op.getFunc().getArguments().size() != 2) {
				throw new OocCompilationError(op, stack,
						"To overload the add operator, you need exactly two arguments, not "
						+op.getFunc().getArgsRepr());
			}
			NodeList<Argument> args = op.getFunc().getArguments();
			Argument first = args.get(0);
			Argument second = args.get(1);			
			if(first.getType().equals(left.getType())) {
				if(second.getType().equals(right.getType()) || isGeneric(second.getType(), op.getFunc().getTypeParams())) {
					FunctionCall call = new FunctionCall(op.getFunc(), startToken);
					call.getArguments().add(left);
					call.getArguments().add(right);
					stack.peek().replace(this, call);
					end = true;
				}
			}
		}
		return end;
	}
	
	private boolean isGeneric(Type type, List<TypeParam> typeParams) {
		for(TypeParam typeParam: typeParams) {
			if(type.getName().equals(typeParam.getName())) return true;
		}
		return false;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == left) {
			left = (Expression) kiddo;
			return true;
		}
		
		if(oldie == right) {
			right = (Expression) kiddo;
			return true;
		}
		
		return false;
	}
	
	public abstract OpType getOpType();
	
}
