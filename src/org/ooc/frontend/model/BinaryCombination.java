package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;

public class BinaryCombination extends BinaryOperation {

	public static enum BinaryComp {
		LOGICAL_OR,
		LOGICAL_AND,
		BITWISE_OR,
		BITWISE_AND,
		BITWISE_XOR,
		LSHIFT,
		RSHIFT,
	}
	
	protected BinaryComp comp;
	
	public BinaryCombination(BinaryComp comp, Expression left, Expression right, Token token) {
		super(left, right, token);
		this.comp = comp;
	}

	@Override
	public Type getType() {
		if(comp == BinaryComp.LOGICAL_OR || comp == BinaryComp.LOGICAL_AND) {
			return BoolLiteral.type;
		}
		return left.getType();
	}
	
	public BinaryComp getComp() {
		return comp;
	}
	
	public void setComp(BinaryComp comp) {
		this.comp = comp;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public String getOpString() {
		switch(comp) {
		case LOGICAL_OR: return "||";
		case LOGICAL_AND: return "&&";
		case BITWISE_XOR: return "^";
		case BITWISE_OR: return "|";
		case BITWISE_AND: return "&";
		case LSHIFT: return "<<";
		case RSHIFT: return ">>";
		}
		return "<op>";
	}
	

	@Override
	public OpType getOpType() {
		switch(comp) {
		case LOGICAL_OR: return OpType.L_OR;
		case LOGICAL_AND: return OpType.L_AND;
		case BITWISE_XOR: return OpType.B_XOR;
		case BITWISE_OR: return OpType.B_OR;
		case BITWISE_AND: return OpType.B_AND;
		case LSHIFT: return OpType.LSHIFT;
		case RSHIFT: return OpType.RSHIFT;
		}
		return null; // never happens
	}

	@Override
	public int getPriority() {
		switch(comp) {
			case LSHIFT: case RSHIFT: return 30;
			case BITWISE_AND: return 60;
			case BITWISE_XOR: return 70;
			case BITWISE_OR:  return 80;
			case LOGICAL_AND: return 90;
			case LOGICAL_OR: return 100;
		}
		return 0; // never happens
	}

}
