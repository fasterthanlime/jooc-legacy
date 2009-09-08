package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class Compare extends BinaryOperation {

	public static enum CompareType {
		GREATER,
		GREATER_OR_EQUAL,
		LESSER,
		LESSER_OR_EQUAL,
		EQUAL,
		NOT_EQUAL,
	}

	protected CompareType compareType;
	
	public Compare(Expression left, Expression right, CompareType compareType, Token token) {
		super(left, right, token);
		this.compareType = compareType;
	}
	
	public CompareType getCompareType() {
		return compareType;
	}
	
	public void setCompareType(CompareType compareType) {
		this.compareType = compareType;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public OpType getOpType() {
		switch(compareType) {
		case EQUAL:
			return OpType.EQ;
		case GREATER:
			return OpType.GT;
		case GREATER_OR_EQUAL:
			return OpType.GTE;
		case LESSER:
			return OpType.LT;
		case LESSER_OR_EQUAL:
			return OpType.LTE;
		case NOT_EQUAL:
			return OpType.NE;
		}
		return null;
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;
	
		boolean isGeneric = false;
		Expression realLeft = left;
		Expression realRight = right;
		Expression size = null;
		if(left.getType().isGeneric()) {
			isGeneric = true;
			realLeft = new AddressOf(left, left.startToken);
			GenericType genericType = (GenericType) left.getType().getRef();
			VariableAccess tAccess = new VariableAccess(genericType.getName(), startToken);
			size = new MemberAccess(tAccess, "size", startToken);
		}
		if(right.getType().isGeneric()) {
			isGeneric = true;
			realRight = new AddressOf(right, right.startToken);
			if(size == null) {
				GenericType genericType = (GenericType) left.getType().getRef();
				VariableAccess tAccess = new VariableAccess(genericType.getName(), startToken);
				size = new MemberAccess(tAccess, "size", startToken);
			}
		}
		
		if(isGeneric) {
			FunctionCall call = new FunctionCall("memcmp", "", startToken);
			NodeList<Expression> args = call.getArguments();
			args.add(realLeft);
			args.add(realRight);
			args.add(size);
			
			left = call;
			right = new IntLiteral(0, Format.DEC, startToken);
			return Response.RESTART;
		}
		
		return Response.OK;
		
	}

}
