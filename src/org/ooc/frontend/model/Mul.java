package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;

public class Mul extends BinaryOperation {

	public Mul(Expression left, Expression right, Token token) {
		super(left, right, token);
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	

	@Override
	public OpType getOpType() {
		return OpType.MUL;
	}

}
