package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Assignment extends BinaryOperation {

	protected boolean dead = false;
	
	public static enum Mode {
		REGULAR,
		ADD,
		SUB,
		DIV,
		MUL,
		B_XOR,
		B_OR,
		B_AND,
		B_LSHIFT,
		B_RSHIFT,
	}
	
	protected Mode mode;
	
	public Assignment(Expression left, Expression right, Token startToken) {
		this(Mode.REGULAR, left, right, startToken);
	}
	
	public Assignment(Mode mode, Expression lvalue, Expression rvalue, Token startToken) {
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
			case B_AND:
				return "&=";
			case B_LSHIFT:
				return "<<=";
			case B_OR:
				return "|=";
			case B_RSHIFT:
				return ">>=";
			case B_XOR:
				return "^=";
			case REGULAR:
				return "=";
		}
		return "unknown";
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
		case B_OR:
			return OpType.B_OR_ASS;
		case B_AND:
			return OpType.B_AND_ASS;
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

		if(dead) return Response.OK;
		
		// if the parent is not a line
		// or the parent is not (a for of which we are not the test)
		// then it's illegal to use an assignment as an expression
		if(!(stack.peek() instanceof Line) && !(stack.peek() instanceof For && ((For) stack.peek()).getTest() != this)) {
			throw new OocCompilationError(this, stack,
					"It's illegal to use an assignment as an expression (here, in a "
					+stack.peek().getClass().getSimpleName()+") Did you mean '==' ?");
		}
		
		if(left.getType() == null || !left.getType().isResolved()) {
			if(fatal) throw new OocCompilationError(left, stack, "Left type of assignment unresolved: "+left+" (btw, stack = "+stack.toString(true));
			return Response.LOOP;
		}
		
		if(right.getType() == null || !left.getType().isResolved()) {
			if(fatal) throw new OocCompilationError(right, stack, "Right type of assignment unresolved: "+right);
			return Response.LOOP;
		}
		
		/*
		if(left.getType().isSuperOf(right.getType())) {
			right = new Cast(right, left.getType(), right.startToken);
		}
		*/
		
		// if we're an assignment from a generic return value
        // we need to set the returnArg to left and disappear! =)
        if(right instanceof FunctionCall) {
            FunctionCall fCall = (FunctionCall) right;
            FunctionDecl fDecl = fCall.getImpl();
            if(fDecl == null || !fDecl.getReturnType().isResolved()) {
            	if(res.fatal) {
            		throw new OocCompilationError(this, stack, "Need more info on fDecl");
            	}
            	return Response.LOOP;
            }
            
            if(fDecl.getReturnType().isGeneric()) {
            	fCall.setReturnArg(left.getGenericOperand());
                stack.peek().replace(this, fCall);
                if(res.fatal) {
            		throw new OocCompilationError(this, stack, "Just replaced ourselves with fCall, need to restart");
            	}
            	return Response.LOOP;
            }
        }
        
        if(isGeneric()) {
        	MemberAccess sizeAcc = new MemberAccess(new VariableAccess(left.getType().getName(), startToken), "size", startToken);
            
        	FunctionCall fCall = new FunctionCall("memcpy", startToken);
            fCall.getArguments().add(left. getGenericOperand());
            fCall.getArguments().add(right.getGenericOperand());
            fCall.getArguments().add(sizeAcc);
            boolean result = stack.peek().replace(this, fCall);
            
            if(!result) {
                if(res.fatal) throw new OocCompilationError(this, stack, "Couldn't replace ourselves ("+this
                		+") with a memcpy/assignment in a "+stack.peek().getClass().getName()+"! trail = " +stack.toString(true));
            }
            
            // Replaced ourselves, need to tidy up
            return Response.LOOP;
        }
		
		return super.resolve(stack, res, fatal);
		
	}
	
	private boolean isGeneric() {
        return (left. getType().isGeneric() && left. getType().getPointerLevel() == 0) ||
        	   (right.getType().isGeneric() && right.getType().getPointerLevel() == 0);
    }

	@Override
	public int getPriority() {
		return 120;
	}
	
}
