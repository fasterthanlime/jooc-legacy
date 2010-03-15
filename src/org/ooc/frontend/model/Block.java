package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class Block extends Statement implements Scope {

	protected final NodeList<Line> body;
	
	public Block(Token startToken) {
		super(startToken);
		body = new NodeList<Line>(startToken);
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		body.accept(visitor);
	}

	public boolean hasChildren() {
		return body.hasChildren();
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	public NodeList<Line> getBody() {
		return body;
	}

	public FunctionDecl getFunction(String name, String suffix,
			FunctionCall call) {
		return null;
	}

	public void getFunctions(NodeList<FunctionDecl> functions) {}

	public VariableDecl getVariable(String name, VariableAccess victim) {
		return getVariable(body, name);
	}

	public void getVariables(NodeList<VariableDecl> variables) {
		getVariables(body, variables);
	}

}
