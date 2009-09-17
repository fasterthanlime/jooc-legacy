package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class MemberAccess extends VariableAccess {

	protected boolean dead = false;
	protected Expression expression;

	public MemberAccess(String variable, Token startToken) {
		this(new VariableAccess("this", startToken), variable, startToken);
	}
	
	public MemberAccess(Expression expression, String variable, Token startToken) {
		super(variable, startToken);
		this.expression = expression;
	}
	
	public MemberAccess(Expression expression, VariableAccess variableAccess, Token startToken) {
		super(variableAccess.getName(), startToken);
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
		super.acceptChildren(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
	
		if(super.replace(oldie, kiddo)) return true;
		
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Type exprType = expression.getType();
		if(exprType == null) {
			int typeIndex = stack.find(TypeDecl.class);
			if(typeIndex != -1) {
				TypeDecl typeDecl = (TypeDecl) stack.get(typeIndex);
				VariableDecl var = typeDecl.getVariable(getName());
				if(var != null && var.isStatic()) {
					ref = var;
					if(expression instanceof VariableAccess) {
						VariableAccess varAcc = (VariableAccess) expression;
						if(((VariableAccess) expression).getName().equals("this")) {
							varAcc = new VariableAccess(typeDecl.getName(), expression.startToken);
							varAcc.resolve(stack, res, fatal);
							expression = varAcc;
						}
					}
					return Response.OK;
				}
			}
			
			if(fatal) {
				throw new OocCompilationError(this, stack, "Accessing member "
						+getName()+" in an expression "+expression
						+" which type hasn't been resolved yet!");
			}
			return Response.LOOP;
		}
		exprType = exprType.getFlatType(res);
		if(exprType.getRef() == null) exprType.resolve(res);

		if(!tryResolve(stack, exprType, res)) {
			tryResolve(stack, exprType.getFlatType(res), res);
		}
		
		if(ref != null && ref.getType() == null && ref instanceof MustBeResolved) {
			MustBeResolved must = (MustBeResolved) ref;
			must.resolve(stack, res, fatal);
		}
		
		if(ref != null) {
			if(expression instanceof VariableAccess) {
				VariableAccess varAcc = (VariableAccess) expression;
				if(varAcc.getRef() instanceof TypeDecl && !(varAcc.getRef() instanceof TypeParam)) {
					if(ref instanceof VariableDecl) {
						VariableDecl varDecl = (VariableDecl) ref;
						if(!varDecl.isStatic() && !ref.getName().equals("class")) {
							throw new OocCompilationError(this, stack, 
									"Trying to access member variable "+exprType
									+"."+getName()+" as if it were static. But it's not. (btw, expression = "+varAcc.getRef());
						}
					}
				}
			}
			if(ref instanceof FunctionDecl && !expression.getType().getName().equals("Class")) {
				MemberAccess membAcc = new MemberAccess(expression, "class", startToken);
				membAcc.resolve(stack, res, fatal);
				expression = membAcc;
			}
		}
		
		if(fatal && ref == null && !dead) {
			String message = "Can't resolve access to member "+exprType+"."+getName();
			String guess = guessCorrectName((TypeDecl) exprType.getRef());
			if(guess != null) {
				message += " Did you mean "+exprType+"."+guess+" ?";
			}
			throw new OocCompilationError(this, stack, message);
		}
		
		return (ref == null && !dead) ? Response.LOOP : Response.OK;
	}

	private String guessCorrectName(final TypeDecl typeDeclaration) {
		
		if(typeDeclaration == null) {
			return null;
		}
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		for(VariableDecl decl: typeDeclaration.getVariables()) {
			for(VariableDeclAtom atom: decl.atoms) {
				int distance = Levenshtein.distance(getName(), atom.getName());
				if(distance < bestDistance) {
					bestDistance = distance;
					bestMatch = atom.getName();
				}
			}
		}
		
		return bestMatch;
		
	}

	private boolean tryResolve(NodeList<Node> stack, Type exprType, Resolver res) {
		
		if(dead) return true;
		
		Declaration decl = exprType.getRef();
		if(decl == null) {
			return false;
		}
		
		if(!(decl instanceof TypeDecl)) {
			throw new OocCompilationError(this, stack,
					"Trying to access to a member of not a TypeDecl, but a "+decl);
		}
		
		TypeDecl typeDecl = (TypeDecl) decl;
		if(ref == null) {
			ref = typeDecl.getVariable(getName());
		}
		
		if(ref == null && getName().equals("size") && exprType.isArray()) {
			FunctionCall sizeofArray = new FunctionCall("sizeof", "", startToken);
			sizeofArray.getArguments().add(expression);
			FunctionCall sizeofType = new FunctionCall("sizeof", "", startToken);
			 // FIXME it should probably be type.dereference()
			sizeofType.getArguments().add(new VariableAccess(expression.getType().getName(), startToken)); 
			Div div = new Div(sizeofArray, sizeofType, startToken);
			stack.peek().replace(this, new Parenthesis(div, startToken));
			return true;
		}

		if(ref == null && exprType.getRef() instanceof CoverDecl && getName().equals("class")) {
			MemberCall membCall = new MemberCall(expression, "class", "", startToken);
			if(!stack.peek().replace(this, membCall)) {
				throw new OocCompilationError(this, stack, "Couldn't replace class access with member call");
			}
			membCall.resolve(stack, res, true);
			dead = true;
			return false;
		}
		
		if(exprType.getRef() instanceof TypeDecl
				&& (getName().equals("getName()") || getName().equals("size") || getName().equals("super")
						 || getName().equals("size"))) {
			if(!exprType.getName().equals("Class")) {
				MemberAccess membAcc = new MemberAccess(expression, "class", startToken);
				this.expression = membAcc;
				stack.push(this);
				membAcc.resolve(stack, res, true);
				stack.pop(this);
				tryResolve(stack, expression.getType(), res);
				return true;
			}
		}
		
		if(ref == null) {
			ref = typeDecl.getFunction(getName(), "", null);
		}
		
		return ref != null;
		
	}
	
	@Override
	public String toString() {
		if(expression instanceof VariableAccess) {
			VariableAccess varAcc = (VariableAccess) expression;
			return getClass().getSimpleName()+" "+varAcc.getName()+":"+varAcc.getType()
			+"->"+getName()+":"+getType();
		}
		
		return "MemberAccess|"+expression+"."+getName()+":"+getType();
	}

}
