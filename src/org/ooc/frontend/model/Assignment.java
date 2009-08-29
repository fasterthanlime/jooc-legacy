package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;

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
	
}
