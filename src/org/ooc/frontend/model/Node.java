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
	
	public String generateTempName(String nameSeedParam, NodeList<Node> stack) {
		String nameSeed = nameSeedParam.toLowerCase(); // in case we get things like Tparam
		int seedNumber = 0;
		String tmpName = nameSeed;
		while((getVariable(tmpName, stack) != null) || (getFunction(tmpName, "", null, stack) != null)) {
			seedNumber++;
			tmpName = nameSeed + seedNumber;
		}
		return tmpName;
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
		return getFunction(name, suffix, call, stack, stack.find(Scope.class), 0, null);
	}

	public FunctionDecl getFunction(String name, String suffix, FunctionCall call, NodeList<Node> stack, int index,
			int bestScoreParam, FunctionDecl bestMatchParam) {
		int bestScore = bestScoreParam;
		FunctionDecl bestMatch = bestMatchParam;
		if(index == -1) return bestMatch;
		
		FunctionDecl function = ((Scope) stack.get(index)).getFunction(name, suffix, call);
		if(function != null) {
			if(call == null) return function;
			int score = call.getScore(function);
			if(score > bestScore) {
				bestScore = score;
				bestMatch = function;
			}
		}
		return getFunction(name, suffix, call, stack, stack.find(Scope.class, index - 1), bestScore, bestMatch);
	}
	
	public TypeParam getTypeParam(NodeList<Node> stack, String typeName) {
		int genIndex = stack.find(Generic.class);
		while(genIndex != -1) {
			Generic gen = (Generic) stack.get(genIndex);
			TypeParam genType = gen.getTypeParams().get(typeName);
			if(genType != null) return genType;
			genIndex = stack.find(Generic.class, genIndex - 1);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void addAfterLine(NodeList<Node> stack, Statement statement) {
		
		int lineIndex = stack.find(Line.class);
		Line line = (Line) stack.get(lineIndex);
		NodeList<Line> list = (NodeList<Line>) stack.get(lineIndex - 1);
		list.addAfter(line, new Line(statement));
		
	}
	
	@SuppressWarnings("unchecked")
	public void addBeforeLine(NodeList<Node> stack, Statement statement) {
		
		int lineIndex = stack.find(Line.class);
		Line line = (Line) stack.get(lineIndex);
		NodeList<Line> list = (NodeList<Line>) stack.get(lineIndex - 1);
		list.addBefore(line, new Line(statement));
		
	}
	
	public VariableDecl getVariable(NodeList<Line> body, String name) {
		if(body.size() > 0) for(Line line: body) {
			Node node = line.getStatement();
			if(node instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl) node;
				if(varDecl.hasAtom(name)) return varDecl;
			}
		}			
		return null;
	}
	
	public void getVariables(NodeList<Line> body, NodeList<VariableDecl> variables) {
		if(body.size() > 0) for(Line line: body) {
			Node node = line.getStatement();
			if(node instanceof VariableDecl) {
				variables.add((VariableDecl) node);
			}
		}
	}
	
	public boolean canBeReferenced() {
		return true;
	}
	
}
