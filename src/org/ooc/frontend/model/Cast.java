package org.ooc.frontend.model;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Cast extends Expression implements MustBeResolved {

	static enum CastMode {
		REGULAR,
		ARRAY, // for array literals
		MAP, // for map literal
	}
	
	protected Expression expression;
	protected Type type;
	
	public Cast(Expression expression, Type targetType, Token startToken) {
		super(startToken);
		this.expression = expression;
		setType(targetType);
	}
	
	@Override
	public Expression getGenericOperand() {
		// FIXME: hmm not really correct but fixes more thing than it breaks atm.
		return expression.getGenericOperand();
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	public Type getType() {
		return type;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
		expression.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public void setType(Type newType) {
		this.type = newType;
		if(type.isGenericRecursive() && expression.getType() != null && expression.getType().isGenericRecursive()) {
			type = type.clone();
			TypeDecl dstDecl = (TypeDecl) newType.getRef();
			Type src = expression.getType();
			if(dstDecl != null && dstDecl.getTypeParams() != null
				&& src != null && src.getTypeParams() != null
				&& dstDecl.getTypeParams().size() != src.getTypeParams().size()) {
				throw new Error("Invalid cast between types "+dstDecl.getType()+" and "+src);
			}
			if(src != null) {
				type.getTypeParams().clear();
				type.getTypeParams().addAll(src.getTypeParams());
			}
		}
	}
	
	@Override
	public Expression getInner() {
		return getExpression();
	}
	
	@Override
	public String toString() {
		return "["+expression+" as "+type+"]";
	}
	
	@Override
	public boolean canBeReferenced() {
		return expression.canBeReferenced();
	}

	public boolean isResolved() {
		return false;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Response response;
		
		Expression realExpr = expression.bitchJumpCasts();
		
		if(realExpr.getType() == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Couldn't resolve type of expression in a cast");
			}
			return Response.LOOP;
		}
		
		//if(realExpr instanceof ArrayLiteral) {
		if(realExpr.getType().getPointerLevel() > 0) {
			response = tryArrayOverload(stack, res, fatal);
			if(response != Response.OK) return response;
		}
		
		response = tryRegularOverload(stack, res, fatal);
		if(response != Response.OK) return response;
		
		return Response.OK;
		
	}
	
	private Response tryRegularOverload(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Type leftType = expression.getType();
		Type rightType = getType();
		if(!leftType.isResolved() || !rightType.isResolved()) {
			//System.out.println("Bitch-looping because either "+leftType+" or "+rightType+" isn't resolved");
			return Response.LOOP;
		}
		
		OpDecl bestOp = null;
		int bestScore = 0;
		for(OpDecl op: res.module.getOps()) {
			int score = getRegularOpScore(stack, OpType.AS, op, res, leftType, rightType);
			if(score > bestScore) {
				bestScore = score;
				bestOp = op;
			}
		}
		for(Import imp: res.module.getAllImports()) { // TODO: really all imports?
			for(OpDecl op: imp.getModule().getOps()) {
				int score = getRegularOpScore(stack, OpType.AS, op, res, leftType, rightType);
				if(score > bestScore) {
					bestScore = score;
					bestOp = op;
				}
			}
		}
		
		if(bestOp != null) {
			FunctionCall call = new FunctionCall(bestOp.getFunc(), startToken);
			call.getArguments().add(expression);
			Node parent = stack.peek();
			parent.replace(this, call);
			call.resolve(stack, res, true);
			return Response.LOOP;
		}
		
		return Response.OK;
		
	}

	private int getRegularOpScore(NodeList<Node> stack, OpType opType, OpDecl op, Resolver res, Type leftType, Type rightType) {
		
		int score = 0;
		if(op.getOpType() == opType) {
			NodeList<Argument> args = op.getFunc().getArguments();
			if(args.size() == 2) return score; // not for us
			if(args.size() != 1) {
				throw new OocCompilationError(op, stack,
						"To overload the "+opType.toPrettyString()+" operator, you need exactly one arguments, not "
						+op.getFunc().getArgsRepr());
			}
			Type firstType = args.get(0).getType();
			Type secondType = op.getFunc().getReturnType();
			if(firstType.softEquals(leftType, res)) {
				if(secondType.softEquals(rightType, res) || isGeneric(secondType, op.getFunc().getTypeParams())) {
					score += 10;
					if(firstType.equals(leftType)) {
						score += 20;
					}
					if(secondType.equals(rightType)) {
						score += 20;
					}
				}
			}
		}
		
		return score;
		
	}
	
	private Response tryArrayOverload(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Type leftType = expression.getType();
		Type rightType = getType();
		if(!leftType.isResolved() || !rightType.isResolved()) {
			//System.out.println("Bitch-looping because either "+leftType+" or "+rightType+" isn't resolved");
			return Response.LOOP;
		}
		
		OpDecl bestOp = null;
		int bestScore = 0;
		for(OpDecl op: res.module.getOps()) {
			int score = getArrayOpScore(stack, OpType.AS, op, res, leftType, rightType);
			if(score > bestScore) {
				bestScore = score;
				bestOp = op;
			}
		}
		for(Import imp: res.module.getAllImports()) { // TODO: all imports?
			for(OpDecl op: imp.getModule().getOps()) {
				int score = getArrayOpScore(stack, OpType.AS, op, res, leftType, rightType);
				if(score > bestScore) {
					bestScore = score;
					bestOp = op;
				}
			}
		}
		
		if(bestOp != null) {
			Type innerType = expression.getType().dereference();
			int numElements = -1;
			
			if(expression instanceof VariableAccess) {
				expression = ((VariableAccess) expression).getRef();
				if(expression instanceof VariableDeclFromExpr) {
					VariableDeclFromExpr vdfe = (VariableDeclFromExpr) expression;
					expression = vdfe.getAtoms().getFirst().getExpression();
				}
			}
			
			if(expression instanceof ArrayLiteral) {
				ArrayLiteral lit = (ArrayLiteral) expression;
				if(lit.getInnerType() == null) {
					if(fatal) {
						throw new OocCompilationError(lit, stack,
								"Couldn't resolve inner type of ArrayLiteral, can't correctly call the overloaded cast!");
					}
					return Response.LOOP;
				}
				numElements = lit.getElements().size();
			} else {
				throw new OocCompilationError(expression, stack, "Trying to array-cast to " + getType() 
						+ " an array of which we don't know the size! Try a constructor instead, passing the" +
						  " size explicitly as an argument.");
			}
			
			FunctionCall call = new FunctionCall(bestOp.getFunc(), startToken);
			call.getArguments().add(expression);
			call.getArguments().add(new IntLiteral(numElements, IntLiteral.Format.DEC, expression.startToken));
			TypeAccess typeAccess = new TypeAccess(innerType, expression.startToken);
			call.getTypeParams().add(typeAccess);
			typeAccess.resolve(stack, res, true);
			Node parent = stack.peek();
			parent.replace(this, call);
			Response resp2 = Response.RESTART;
			while(resp2 == Response.RESTART) {
				resp2 = call.resolve(stack, res, true);
			}
			return Response.LOOP;
		}
		
		return Response.OK;
		
	}
	
	private int getArrayOpScore(NodeList<Node> stack, OpType opType, OpDecl op, Resolver res, Type leftType, Type rightType) {
		
		int score = 0;
		if(op.getOpType() == opType) {
			NodeList<Argument> args = op.getFunc().getArguments();
			if(args.size() == 1) return score; // not for us
			if(args.size() > 2 || args.size() < 1) {
				throw new OocCompilationError(op, stack,
						"To overload the "+opType.toPrettyString()+" operator from arrays, you need exactly two arguments (T* and size), not "
						+op.getFunc().getArgsRepr());
			}
			
			Type firstType = args.get(0).getType();
			Type secondType = op.getFunc().getReturnType();
			if(secondType.softEquals(rightType, res) || isGeneric(secondType, op.getFunc().getTypeParams())) {
				score += 10;
				if(firstType.equals(leftType)) {
					score += 20;
				}
				if(secondType.equals(rightType)) {
					score += 20;
				}
			}
		}
		
		return score;
		
	}
	
	private boolean isGeneric(Type type, LinkedHashMap<String, TypeParam> linkedHashMap) {
		return linkedHashMap.containsKey(type.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Node> T bitchJumpCasts() {
		return (T) expression;
	}

}
