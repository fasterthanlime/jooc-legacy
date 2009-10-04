package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;

public class Add extends BinaryOperation {

	public Add(Expression left, Expression right, Token startToken) {
		super(left, right, startToken);
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public OpType getOpType() {
		return OpType.ADD;
	}

	@Override
	public int getPriority() {
		return 20;
	}
	
}
