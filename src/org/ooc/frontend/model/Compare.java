package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
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
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;

		if(left.getType() == null || !left.getType().isResolved()) {
			if(fatal) throw new OocCompilationError(left, stack, "Left type of assignment unresolved: "+left+" (btw, stack = "+stack.toString(true));
			return Response.LOOP;
		}
		
		if(right.getType() == null || !left.getType().isResolved()) {
			if(fatal) throw new OocCompilationError(right, stack, "Right type of assignment unresolved: "+right);
			return Response.LOOP;
		}
		
		VariableAccess tAccess = new VariableAccess(left.getType().getRef().getName(), startToken);
		Expression size = new MemberAccess(tAccess, "size", startToken);
		
		if(isGeneric()) {
			FunctionCall call = new FunctionCall("memcmp", startToken);
			NodeList<Expression> args = call.getArguments();
			args.add(left.getGenericOperand());
			args.add(right.getGenericOperand());
			args.add(size);
			
			left = call;
			right = new IntLiteral(0, Format.DEC, startToken);
			return Response.RESTART;
		}
		
		return Response.OK;
		
	}
	
	private boolean isGeneric() {
        return (left. getType().isGeneric() && left. getType().getPointerLevel() == 0) ||
        	   (right.getType().isGeneric() && right.getType().getPointerLevel() == 0);
    }

	@Override
	public int getPriority() {
		switch(compareType) {
			case GREATER: case GREATER_OR_EQUAL: case LESSER: case LESSER_OR_EQUAL: return 40;
			case EQUAL: case NOT_EQUAL: return 50; 
		}
		return 0; // never happens
	}
	
}
