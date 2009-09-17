package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Assignment extends BinaryOperation {

	public static enum Mode {
		REGULAR,
		ADD,
		SUB,
		DIV,
		MUL,
		B_XOR,
		B_LSHIFT,
		B_RSHIFT,
	}
	
	protected Mode mode;
	
	public Assignment(Access left, Expression right, Token startToken) {
		this(Mode.REGULAR, left, right, startToken);
	}
	
	public Assignment(Mode mode, Access lvalue, Expression rvalue, Token startToken) {
		super(lvalue, rvalue, startToken);
		this.mode = mode;
		this.left = lvalue;
		this.right = rvalue;
	}
	
	public Mode getMode() {
		return mode;
	}

	@Override
	public Type getType() {
		return left.getType();
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
		left.accept(visitor);
		right.accept(visitor);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == left) {
			left = (Access) kiddo;
			return true;
		}
		if(oldie == right) {
			right = (Expression) kiddo;
			return true;
		}
		return false;
	}

	public String getSymbol() {
		switch(mode) {
			case ADD:
				return "+=";
			case DIV:
				return "/=";
			case MUL:
				return "*=";
			case SUB:
				return "-=";
			default:
				return "=";
		}
	}

	@Override
	public OpType getOpType() {
		switch(mode) {
		case ADD:
			return OpType.ADD_ASS;
		case DIV:
			return OpType.DIV_ASS;
		case MUL:
			return OpType.MUL_ASS;
		case SUB:
			return OpType.SUB_ASS;
		case B_XOR:
			return OpType.B_XOR_ASS;
		case B_LSHIFT:
			return OpType.B_LSHIFT_ASS;
		case B_RSHIFT:
			return OpType.B_RSHIFT_ASS;
		case REGULAR:
			return OpType.ASS;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Assignment: "+left.toString()+" = "+right.toString();
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
	
		if(right.getType() != null && left.getType() != null) {
			if(left.getType().isSuperOf(right.getType())) {
				right = new Cast(right, left.getType(), right.startToken);
			}
		}
		
		if(left.getType() == null) {
			if(fatal) throw new OocCompilationError(left, stack, "Left type of assignment unresolved: "+left);
			return Response.LOOP;
		}
		if(right.getType() == null) {
			if(fatal) throw new OocCompilationError(right, stack, "Right type of assignment unresolved: "+right);
			return Response.LOOP;
		}
		
		boolean isGeneric = false;
		Expression realLeft = null;
		Expression realRight = null;
		Expression size = null;
		if(left.getType().isGeneric()) {
			isGeneric = true;
			TypeParam genericType = (TypeParam) left.getType().getRef();
			VariableAccess tAccess = new VariableAccess(genericType.getName(), startToken);
			size = new MemberAccess(tAccess, "size", startToken);
			realLeft = new AddressOf(left, left.startToken);
		}
		if(right.getType().isGeneric()) {
			isGeneric = true;
			realRight = new AddressOf(right, right.startToken);
		}
		if(isGeneric) {
			if(left instanceof ArrayAccess) {
				ArrayAccess arrAcc = (ArrayAccess) left;
				Expression var = arrAcc.getVariable();
				if(var.getType().isGeneric()) {
					VariableAccess tAccess = new VariableAccess(var.getType().getRef().getName(), startToken);
					MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
					realLeft = new Add(arrAcc.variable, new Mul(arrAcc.index, sizeAccess, startToken), startToken);
				}
			}
			if(right instanceof ArrayAccess) {
				ArrayAccess arrAcc = (ArrayAccess) right;
				Expression var = arrAcc.getVariable();
				if(var.getType().isGeneric()) {
					VariableAccess tAccess = new VariableAccess(var.getType().getRef().getName(), startToken);
					MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
					realRight = new Add(arrAcc.variable, new Mul(arrAcc.index, sizeAccess, startToken), startToken);
				}
			}
			if(realLeft != null && realRight != null && size != null) {
				unwrapToMemcpy(stack, realLeft, realRight, size);
				return Response.RESTART;
			}
		}
		
		return super.resolve(stack, res, fatal);
		
	}

	private void unwrapToMemcpy(NodeList<Node> stack, Expression realLeft, Expression realRight, Expression size) {
		FunctionCall call = new FunctionCall("memcpy", "", startToken);
		NodeList<Expression> args = call.getArguments();
		if(realLeft == null || realRight == null || size == null) {
			throw new Error("Heh :/ either of those are null: realLeft = "+realLeft
					+", realRight = "+realRight+", size = "+size);
		}
		args.add(realLeft);
		args.add(realRight);
		args.add(size);
		stack.peek().replace(this, call);
	}
	
}
