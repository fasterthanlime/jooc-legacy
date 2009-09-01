package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
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
	public boolean resolve(NodeList<Node> mainStack, Resolver res, final boolean fatal) throws IOException {

		Type exprType = expression.getType();
		if(exprType == null) {
			if(fatal) {
				throw new OocCompilationError(this, mainStack, "Calling member function "
						+name+getArgsRepr()+" in an expression "+expression.getClass().getSimpleName()
						+" which type hasn't been resolved yet!");
			}
			return true;
		}
		exprType = exprType.getFlatType(res);
		if(exprType.getRef() == null) exprType.resolve(res);
		
		if(exprType.getRef() == null) {
			if(fatal) {
				throw new OocCompilationError(this, mainStack, "Calling member function "
						+name+getArgsRepr()+" in an expression "+expression
						+" which type hasn't been ref'd yet. Its type = "+exprType);
			}
			return true;
		}
		Declaration decl = exprType.getRef();
		if(!(decl instanceof TypeDecl)) {
			throw new OocCompilationError(this, mainStack, 
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
		
		if(fatal && impl == null) {
			String message = "Couldn't resolve call to function "
				+typeDeclaration.getInstanceType()+"."+name+getArgsRepr()+".";
			String guess = guessCorrectName(typeDeclaration);
			if(guess != null) {
				message += " Did you mean "+typeDeclaration.getInstanceType()+"."+guess+" ?";
			}
			message += "\nExisting funcs in type "+typeDeclaration.getInstanceType()+": "+typeDeclaration.getFunctionsRepr();
			throw new OocCompilationError(this, mainStack, message);
		}
		
		return impl == null;
		
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
				bestMatch = decl.getProtoRepr();
			}
		}
		
		return bestMatch;
		
	}
	
}
