package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ArrayAccess extends Access implements MustBeResolved {

	Type type;
	protected Expression variable;
	protected NodeList<Expression> indices;

	public ArrayAccess(Expression variable, Token startToken) {
		super(startToken);
		this.variable = variable;
		this.indices =  new NodeList<Expression>(startToken);
	}
	
	public Expression getVariable() {
		return variable;
	}
	
	public void setVariable(Expression variable) {
		this.variable = variable;
	}
	
	public NodeList<Expression> getIndices() {
		return indices;
	}

	public Type getType() {
		if(type == null) {
			Type exprType = variable.getType();
			if(exprType != null) {
				Declaration ref = exprType.getRef();
				if(ref instanceof CoverDecl) {
					Type fromType = ((CoverDecl) ref).getFromType();
					if(fromType != null && fromType.getRef() instanceof CoverDecl) {
						Type clone = fromType.clone();
						clone.setPointerLevel(exprType.getPointerLevel() + fromType.getPointerLevel());
						exprType = clone;
					}
				}
				type = new Type(exprType.getName(), exprType.getPointerLevel() - 1, exprType.startToken);
				type.setRef(exprType.getRef());
			}
		}
		return type;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		variable.accept(visitor);
		indices.accept(visitor);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == variable) {
			variable = (Expression) kiddo;
			return true;
		}
		
		if(oldie == indices) {
			indices = (NodeList<Expression>) kiddo;
			return true;
		}
		
		return false;
		
	}

	public boolean isResolved() {
		return false;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		int assignIndex = -1;
		
		if(stack.peek() instanceof Assignment) {
			Assignment ass = (Assignment) stack.peek();
			if(ass.getLeft() == this) {
				assignIndex = stack.size() - 1;
			} else {
				NodeList<Node> copy = new NodeList<Node>();
				copy.addAll(stack);
				copy.pop();
				Response response = ass.resolve(copy, res, fatal);
				if(response != Response.OK) {
					return response;
				}
			}
		}
		
		for(OpDecl op: res.module.getOps()) {
			if(tryOp(stack, res, assignIndex, op)) {
					//return Response.RESTART;
					return Response.LOOP;
			}
		}
		for(Import imp: res.module.getImports()) {
			for(OpDecl op: imp.getModule().getOps()) {
				if(tryOp(stack, res, assignIndex, op)) { 
					//return Response.RESTART;
					return Response.LOOP;
				}
			}
		}
		return Response.OK;
		
	}

	private boolean tryOp(NodeList<Node> stack, Resolver res, int assignIndex, OpDecl op)
		throws OocCompilationError {
		
		if(assignIndex == -1) {
			if(tryIndexing(op, stack, res)) return true;
		} else {
			if(tryIndexedAssign(op, stack, res, assignIndex)) return true;
		}
		return false;
		
	}

	protected boolean tryIndexedAssign(OpDecl op, NodeList<Node> stack, Resolver res, int assignIndex) throws OocCompilationError {
		
		if(op.getOpType() != OpType.IDX_ASS) return false;
		
		Assignment ass = (Assignment) stack.get(assignIndex);
		if(ass.getLeft() != this) {
			return false;
		}
		
		if(op.getFunc().getArguments().size() != 3) {
			throw new OocCompilationError(op, stack,
					"To overload the indexed assign operator, you need exactly three arguments, not "
					+op.getFunc().getArgsRepr());
		}
		NodeList<Argument> args = op.getFunc().getArguments();
		if(args.get(0).getType().softEquals(variable.getType(), res)
				&& args.get(1).getType().softEquals(indices.getFirst().getType(), res)) {
			FunctionCall call = new FunctionCall(op.getFunc(), startToken);
			call.getArguments().add(variable);
			call.getArguments().addAll(indices);
			call.getArguments().add(ass.getRight());
			if(!stack.get(assignIndex - 1).replace(ass, call)) {
				System.out.println("stack = "+stack.toString(true));
				Thread.dumpStack();
				throw new OocCompilationError(this, stack, "Couldn't replace array-access-assign with a function call");
			}
			return true;
		}
		
		return false;
		
	}

	protected boolean tryIndexing(OpDecl op, NodeList<Node> stack, Resolver res) throws OocCompilationError {
		
		if(op.getOpType() != OpType.IDX) return false;
		
		if(op.getFunc().getArguments().size() != 2) {
			throw new OocCompilationError(op, stack,
					"To overload the indexing operator, you need exactly two arguments, not "
					+op.getFunc().getArgsRepr());
		}
		NodeList<Argument> args = op.getFunc().getArguments();
		if(args.get(0).getType().softEquals(variable.getType(), res)
		&& args.get(1).getType().softEquals(indices.getFirst().getType(), res)) {
			FunctionCall call = new FunctionCall(op.getFunc(), startToken);
			call.getArguments().add(variable);
			call.getArguments().addAll(indices);
			stack.peek().replace(this, call);
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public String toString() {
		return variable.toString() + indices;
	}
	
}
