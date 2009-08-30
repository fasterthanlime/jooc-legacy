package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Node implements Visitable {

	public final transient Token startToken;
	
	public Node(Token startToken) {
		this.startToken = startToken;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public abstract boolean replace(Node oldie, Node kiddo);
	
	public String generateTempName(String originSeed, NodeList<Node> stack) {
		String seed = originSeed.toLowerCase();
		String name = seed;
		int i = 1;
		while(hasVariable(name, stack)) {
			name = seed + (i++);
		}
		return name;
	}

	public boolean hasVariable(String name, NodeList<Node> stack) {
		return getVariable(name, stack) != null;
	}

	public VariableDecl getVariable(String name, NodeList<Node> stack) {
		return getVariable(name, stack, stack.find(Scope.class));
	}

	public VariableDecl getVariable(String name, NodeList<Node> stack, int index) {
		if(index == -1) return null;
		VariableDecl varDecl = ((Scope) stack.get(index)).getVariable(name);
		if(varDecl != null) return varDecl;
		return getVariable(name, stack, stack.find(Scope.class, index - 1));
	}
	
	public FunctionDecl getFunction(String name, FunctionCall call, NodeList<Node> stack) {
		return getFunction(name, call, stack, stack.find(Scope.class));
	}

	public FunctionDecl getFunction(String name, FunctionCall call, NodeList<Node> stack, int index) {
		if(index == -1) return null;
		FunctionDecl func = ((Scope) stack.get(index)).getFunction(name, call);
		if(func != null) return func;
		return getFunction(name, call, stack, stack.find(Scope.class, index - 1));
	}
	
}
