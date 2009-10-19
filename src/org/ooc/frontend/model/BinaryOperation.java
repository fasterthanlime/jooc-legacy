package org.ooc.frontend.model;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

/**
 * Binary in the sense that it has a left and a right operand (e.g. binary op,
 * as opposed to unary op or ternary op)
 * 
 * Operator precedence chart:
 * 
 *   10   * / %
 *   20   + -
 *   30   << >>
 *   40   < > <= >=
 *   50   == !=
 *   60   &
 *   70   ^
 *   80   |
 *   90   &&
 *   100  ||
 *   110  ?:
 *   120  = += -= /= *= >>= <<= ^= &= |=
 */
public abstract class BinaryOperation extends Expression implements MustBeUnwrapped, MustBeResolved {

	protected Expression left;
	protected Expression right;
	
	public BinaryOperation(Expression left, Expression right, Token startToken) {
		super(startToken);
		this.left = left;
		this.right = right;
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public void setLeft(Expression left) {
		this.left = left;
	}
	
	public Expression getRight() {
		return right;
	}
	
	public void setRight(Expression right) {
		this.right = right;
	}
	
	public Type getType() {
		// FIXME probably not right (haha)
		return getLeft().getType();
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		left.accept(visitor);
		right.accept(visitor);
	}
	
	public boolean isResolved() {
		return false;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(left.getType() == null) {
			if(left instanceof MustBeResolved) {
				((MustBeResolved) left).resolve(stack, res, fatal);
			}
			if(fatal) {
				throw new OocCompilationError(this, stack, "Can't resolve type of left "+
						left+" operand. Wtf?");
			}
			return Response.LOOP;
		}
		
		if(right.getType() == null) {
			if(right instanceof MustBeResolved) {
				((MustBeResolved) right).resolve(stack, res, fatal);
			}
			if(fatal) {
				throw new OocCompilationError(this, stack, "Can't resolve type of right "
						+right+" operand. Seriously.");
			}
			return Response.LOOP;
		}
		
		OpType opType = getOpType();		
		for(OpDecl op: res.module.getOps()) {
			if(tryOp(stack, opType, op, res)) {
				//return Response.RESTART;
				return Response.LOOP;
			}
		}
		for(Import imp: res.module.getImports()) {
			for(OpDecl op: imp.getModule().getOps()) {
				if(tryOp(stack, opType, op, res)) {
					//return Response.RESTART;
					return Response.LOOP;
				}
			}
		}
		
		if(opType.isNumeric() && left.getType().getRef() instanceof ClassDecl && left.getType().getPointerLevel() == 0) {
			throw new OocCompilationError(this, stack, "Using operator "+opType.toPrettyString()+" between non-numeric types."
					+" Maybe you want to overload it? Do it like this: operator "
					+opType.toPrettyString()+" (left: "+left.getType()+", right: "+right.getType()+") { ... }");
		}
		
		return Response.OK;
		
	}

	private boolean tryOp(NodeList<Node> stack, OpType opType, OpDecl op, Resolver res) {
		boolean end = false;
		if(op.getOpType() == opType) {
			if(op.getFunc().getArguments().size() != 2) {
				throw new OocCompilationError(op, stack,
						"To overload the add operator, you need exactly two arguments, not "
						+op.getFunc().getArgsRepr());
			}
			NodeList<Argument> args = op.getFunc().getArguments();
			Argument first = args.get(0);
			Argument second = args.get(1);
			if(first.getType().softEquals(left.getType(), res)) {
				if(second.getType().softEquals(right.getType(), res) || isGeneric(second.getType(), op.getFunc().getTypeParams())) {
					FunctionCall call = new FunctionCall(op.getFunc(), startToken);
					call.getArguments().add(left);
					call.getArguments().add(right);
					Node parent = stack.peek();
					parent.replace(this, call);
					call.resolve(stack, res, true);
					end = true;
				}
			}
		}
		return end;
	}
	
	private boolean isGeneric(Type type, LinkedHashMap<String, TypeParam> linkedHashMap) {
		return linkedHashMap.containsKey(type.getName());
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == left) {
			left = (Expression) kiddo;
			return true;
		}
		
		if(oldie == right) {
			right = (Expression) kiddo;
			return true;
		}
		
		return false;
	}
	
	public boolean unwrap(NodeList<Node> stack) throws IOException {
		
		// Example:
		// Mul(1, Add(2, 3))
		// Should infact be Add(Mul(1, 2), 3)
		
		if(right instanceof BinaryOperation) {
			BinaryOperation opRight = (BinaryOperation) right;
			if(getPriority() < opRight.getPriority()) {
				Expression tmp = opRight.getLeft();
				opRight.setLeft(this);
				this.setRight(tmp);
				stack.peek().replace(this, opRight);
				return true;
			}
		}
		
		return false;
		
	}
	
	public abstract OpType getOpType();
	
	public abstract int getPriority();
	
	@Override
	public String toString() {
		return "(" + left + " " + getOpType().toPrettyString() + " " + right + ")";
	}
	
}
