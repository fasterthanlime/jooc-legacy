package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class AddressOf extends Access implements MustBeResolved {

	Type type;
	Expression expression;
	
	public AddressOf(Expression expression, Token startToken) {
		super(startToken);
		setExpression(expression);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(expression == oldie) {
			expression = (Expression) kiddo;
			return true;
		}
		
		return false;
	}

	public Type getType() {
		if(type == null) {
			Type exprType = expression.getType();
			if(exprType != null && exprType.isResolved()) {
				type = new Type(exprType.getName(), exprType.getPointerLevel() + 1, exprType.startToken);
				type.setRef(exprType.getRef());
			}
		}
		return type;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		expression.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public String toString() {
		return "&("+expression+")";
	}

	public boolean isResolved() {
		return type != null && expression.canBeReferenced();
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(getType() == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Couldn't resolve type of AddressOf "+this);
			}
			return Response.LOOP;
		}		
		
		if(!expression.canBeReferenced()) {
			VariableDeclFromExpr vdfe = new VariableDeclFromExpr(generateTempName("overzealous", stack), expression, expression.startToken, null);
			this.expression = vdfe;
			stack.push(this);
			vdfe.unwrap(stack);
			stack.pop(this);
			return Response.RESTART;
		}
		
		return Response.OK;
			
	}

}
