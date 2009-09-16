package org.ooc.frontend.model;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class MemberCall extends FunctionCall {

	protected Expression expression;

	public MemberCall(Expression expression, String name, String suffix, Token startToken) {
		super(name, suffix, startToken);
		this.expression = expression;
	}
	
	public MemberCall(Expression expression, FunctionCall call, Token startToken) {
		super(call.name, call.suffix, startToken);
		this.expression = expression;
		arguments.addAll(call.getArguments());
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
	public boolean hasChildren() {
		return true;
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
	public Response resolve(NodeList<Node> stack, Resolver res, final boolean fatal) throws IOException {

		Type exprType = expression.getType();
		if(exprType == null) {
			if(expression instanceof MustBeResolved) {
				MustBeResolved must = (MustBeResolved) expression;
				stack.push(this);
				must.resolve(stack, res, true);
				stack.pop(this);
				exprType = expression.getType();
			}
		}
		
		if(exprType == null) { // still null?
			if(fatal) {
				throw new OocCompilationError(this, stack, "Calling member function "
						+name+getArgsRepr()+" in an expression "+expression
						+" which type hasn't been resolved yet!");
			}
			return Response.LOOP;
		}
		exprType = exprType.getFlatType(res);
		if(exprType.getRef() == null) exprType.resolve(res);
		
		if(exprType.getRef() == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Calling member function "
						+name+getArgsRepr()+" in an expression "+expression
						+" which type hasn't been ref'd yet. Its type = "+exprType);
			}
			return Response.LOOP;
		}
		Declaration decl = exprType.getRef();
		if(!(decl instanceof TypeDecl)) {
			throw new OocCompilationError(this, stack, 
					"Trying to call a member function of not a TypeDecl, but a "
					+decl.getClass().getSimpleName());
		}

		TypeDecl typeDeclaration = (TypeDecl) decl;
		impl = typeDeclaration.getFunction(this);
		
		if(impl == null) {
			for(VariableDecl varDecl: typeDeclaration.getVariables()) {
				if(varDecl.getType() instanceof FuncType && varDecl.getName().equals(name)) {
					FuncType funcType = (FuncType) varDecl.getType();
					impl = funcType.getDecl();
				}
			}
		}
		
		/* Dirty work */
		if(impl != null) {
			Response response = handleGenerics(stack, res, fatal);
			if(response != Response.OK) return response;
			autocast();
		}
		
		if(fatal && impl == null) {
			String message = "Couldn't resolve call to function "
				+typeDeclaration.getInstanceType()+"."+name+getArgsRepr()+".";
			String guess = guessCorrectName(typeDeclaration);
			if(guess != null) {
				message += " Did you mean "+guess+" ?";
			}
			throw new OocCompilationError(this, stack, message);
		}
		
		return (impl == null) ? Response.LOOP : Response.OK;
		
	}
	
	protected String guessCorrectName(final TypeDecl typeDeclaration) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		NodeList<FunctionDecl> functions = new NodeList<FunctionDecl>();
		typeDeclaration.getFunctionsRecursive(functions);
		for(FunctionDecl decl: functions) {
			int distance = Levenshtein.distance(name, decl.getName());
			if(distance < bestDistance) {
				bestDistance = distance;
				bestMatch = decl.getProtoRepr(true);
			}
		}
		
		if(bestDistance > 3) return null;
		return bestMatch;
		
	}
	
	@Override
	public String toString() {
		return "("+expression+")"+"->"+getProtoRepr();
	}
	
	@Override
	protected VariableAccess resolveTypeParam(String typeParam, NodeList<Node> stack, boolean fatal) {
		
		Type type = expression.getType();
		if(type != null && !type.getTypeParams().isEmpty()) {
			Declaration ref = type.getRef();
			if(ref instanceof TypeDecl) {
				TypeDecl typeDecl = (TypeDecl) ref;
				LinkedHashMap<String, TypeParam> typeParams = typeDecl.getTypeParams();
				if(!typeParams.isEmpty()) {
					int i = -1;
					for(TypeParam candidate: typeParams.values()) {
						i++;
						if(candidate.getName().equals(typeParam)) {
							VariableAccess result = type.getTypeParams().get(i);
							return result;
						}
					}
				}
			}
		}
		return super.resolveTypeParam(typeParam, stack, fatal);
		
	}
	
	@Override
	public void throwUnresolvedType(NodeList<Node> stack, String typeName) {
		
		throw new OocCompilationError(this, stack,
				"You should specify type parameters of "+expression.getType().getName()
				+" for calling "+getProtoRepr()+". E.g. you could write "+expression.getType().getName()+"<Int>");
		
	}
	
}
