package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Compare.CompareType;
import org.ooc.frontend.model.tokens.Token;

public class Case extends Node {

	private NodeList<Line> body;
	private Expression expr;
	private Match match;
	private boolean isFallthrough;
	
	// it's actually hideous to have to pass match here, but guess what? we have no choice.
	public Case(Expression expr, Match match, boolean isFallthrough, Token startToken) {
		super(startToken);
		if(expr == null) {
			this.expr = null;
		} else if(match.getExpr() instanceof BoolLiteral && ((BoolLiteral) match.getExpr()).getValue() == true) { 
			this.expr = expr;
		} else {
			this.expr = new Compare(new Parenthesis(match.getExpr()),
					new Parenthesis(expr), CompareType.EQUAL, startToken);
		}
		this.body = new NodeList<Line>();
		this.isFallthrough = isFallthrough;
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		if(expr != null) expr.accept(visitor);
		body.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}
	
	public NodeList<Line> getBody() {
		return body;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public boolean isFallthrough() {
		return isFallthrough;
	}

}
