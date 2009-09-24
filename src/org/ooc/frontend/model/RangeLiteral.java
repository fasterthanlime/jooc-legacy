package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class RangeLiteral extends Literal implements MustBeResolved {

	protected Expression lower;
	protected Expression upper;
	protected static Type type = new Type("Range", Token.defaultToken);
	
	public RangeLiteral(Expression lower, Expression upper, Token startToken) {
		super(startToken);
		this.lower = lower;
		this.upper = upper;
	}
	
	public Expression getLower() {
		return lower;
	}
	
	public void setLower(Expression lower) {
		this.lower = lower;
	}
	
	public Expression getUpper() {
		return upper;
	}
	
	public void setUpper(Expression upper) {
		this.upper = upper;
	}

	public Type getType() {
		return type;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		lower.accept(visitor);
		upper.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == lower) {
			lower = (Expression) kiddo;
			return true;
		}
		
		if(oldie == upper) {
			upper = (Expression) kiddo;
			return true;
		}
		
		return false;
		
	}

	public boolean isResolved() {
		return false;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Node parent = stack.peek();
		if(!(parent instanceof Foreach)) {
			MemberCall newCall = new MemberCall(new VariableAccess("Range", startToken), "new", null, startToken);
			newCall.getArguments().add(lower);
			newCall.getArguments().add(upper);
			parent.replace(this, newCall);
			return Response.RESTART;
		}
			
		return Response.OK;
		
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+":"+lower+".."+upper;
	}

}
