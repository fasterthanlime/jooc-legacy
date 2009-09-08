package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class Assignment extends BinaryOperation {

	public static enum Mode {
		REGULAR,
		ADD,
		SUB,
		DIV,
		MUL,
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
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
	
		if(right.getType() != null && left.getType() != null) {
			if(left.getType().isSuperOf(right.getType())) {
				right = new Cast(right, left.getType(), right.startToken);
			}
		}
		
		if(left instanceof ArrayAccess) {
			ArrayAccess arrAcc = (ArrayAccess) left;
			Expression var = arrAcc.getVariable();
			if(var.getType().isGeneric()) {
				GenericType genericType = (GenericType) var.getType().getRef();
				System.out.println("Got left generic "+var);
				
				unwrapToMemcpy(stack, arrAcc, genericType);
				return Response.RESTART;
			}
		}
		
		return super.resolve(stack, res, fatal);
		
	}

	@SuppressWarnings("unchecked")
	private void unwrapToMemcpy(NodeList<Node> stack, ArrayAccess arrAcc, GenericType genericType) {
		FunctionCall call = new FunctionCall("memcpy", "", startToken);
		
		VariableAccess tAccess = new VariableAccess(genericType.getName(), startToken);
		MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
		
		call.getArguments().add(new Add(arrAcc.variable, new Mul(arrAcc.index, sizeAccess, startToken), startToken));
		call.getArguments().add(new AddressOf(right, right.startToken));
		call.getArguments().add(sizeAccess);
		stack.peek().replace(this, call);
		
		int lineIndex = stack.find(Line.class);
		((NodeList<Node>) stack.get(lineIndex - 1)).addAfter(stack.get(lineIndex), new Line(new Return(startToken)));
	}
	
}
