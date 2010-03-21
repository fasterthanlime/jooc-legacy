package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class ControlStatement extends Statement implements Scope {

	protected NodeList<Line> body;
	
	public ControlStatement(Token startToken) {
		super(startToken);
		this.body = new NodeList<Line>(startToken);
	}

	public NodeList<Line> getBody() {
		return body;
	}
	
	public VariableDecl getVariable(String name) {
		if(body.size() > 0) for(Line line: body) {
			Node node = line.getStatement();
			if(node instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl) node;
				if(varDecl.getName().equals(name)) return varDecl;
			}
		}
		return null;
	}
	
	public void getVariables(NodeList<VariableDecl> variables) {
		if(body.size() > 0) for(Line line: body) {
			Node node = line.getStatement();
			if(node instanceof VariableDecl) {
				variables.add((VariableDecl) node);
			}
		}
	}
	
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call) {
		return null;
	}
	
	public void getFunctions(NodeList<FunctionDecl> functions) {}
	
}
