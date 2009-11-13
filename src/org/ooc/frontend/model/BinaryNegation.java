package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;

public class BinaryNegation extends UnaryOperation {

	public BinaryNegation(Expression inner, Token startToken) {
		super(inner, startToken);
	}

	@Override
	public OpType getOpType() {
		return OpType.B_NEG;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

}
