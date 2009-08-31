package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;

public abstract class Node implements Visitable {

	public final transient Token startToken;
	static int seedNumber = 1;
	
	public Node(Token startToken) {
		this.startToken = startToken;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public abstract boolean replace(Node oldie, Node kiddo);
	
	public String generateTempName(String nameSeed) {
		return nameSeed.toLowerCase() + (seedNumber++);
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
	
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call, NodeList<Node> stack) {
		return getFunction(name, suffix, call, stack, stack.find(Scope.class));
	}

	public FunctionDecl getFunction(String name, String suffix, FunctionCall call, NodeList<Node> stack, int index) {
		if(index == -1) return null;
		FunctionDecl func = ((Scope) stack.get(index)).getFunction(name, suffix, call);
		if(func != null) return func;
		return getFunction(name, suffix, call, stack, stack.find(Scope.class, index - 1));
	}
	
	public GenericType getGenericType(NodeList<Node> stack, String paramName) {
		int genIndex = stack.find(Generic.class);
		while(genIndex != -1) {
			Generic gen = (Generic) stack.get(genIndex);
			GenericType genType = gen.getGenericTypes().get(paramName);
			if(genType != null) return genType;
			genIndex = stack.find(Generic.class, genIndex - 1);
		}
		
		return null;
	}
	
}
