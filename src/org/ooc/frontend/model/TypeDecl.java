package org.ooc.frontend.model;

import org.ooc.frontend.model.tokens.Token;


public abstract class TypeDecl extends Declaration implements Scope {

	protected NodeList<VariableDecl> variables;
	protected NodeList<FunctionDecl> functions;
	
	protected String superName;
	protected TypeDecl superRef;
	
	protected Type instanceType;
	
	public TypeDecl(String name, String superName, Token startToken) {
		super(name, startToken);
		this.superName = superName;
		this.variables = new NodeList<VariableDecl>(startToken);
		this.functions = new NodeList<FunctionDecl>(startToken);
		this.instanceType = new Type(name, startToken);
		instanceType.setRef(this);
	}
	
	public String getSuperName() {
		return superName;
	}
	
	public void setSuperName(String superName) {
		this.superName = superName;
	}
	
	public TypeDecl getSuperRef() {
		return superRef;
	}
	
	public void setSuperRef(TypeDecl superRef) {
		this.superRef = superRef;
	}
	
	public Type getInstanceType() {
		return instanceType;
	}
	
	public boolean hasVariables() {
		return !variables.isEmpty();
	}
	
	public boolean hasFunctions() {
		return !functions.isEmpty();
	}
	
	public Iterable<VariableDecl> getVariables() {
		return variables;
	}
	
	@Override
	public void getVariables(NodeList<VariableDecl> variables) {
		variables.addAll(this.variables);
	}
	
	public void addVariable(VariableDecl decl) {
		decl.setTypeDecl(this);
		variables.add(decl);
	}
	
	public Iterable<FunctionDecl> getFunctions() {
		return functions;
	}
	
	public FunctionDecl getFunction(FunctionCall call) {
		return getFunction(call.getName(), call.getSuffix(), call);
	}
	
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call, boolean recursive) {
		for(FunctionDecl func : functions) {
			if(func.getName().equals(name) && (suffix.isEmpty() || func.getSuffix().equals(suffix))
					&& (call == null || call.matches(func))) return func;
		}
		if(recursive && superRef != null) return superRef.getFunction(name, suffix, call);
		return null;
	}
	
	@Override
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call) {
		return getFunction(name, suffix, call, true);
	}
	
	@Override
	public void getFunctions(NodeList<FunctionDecl> functions) {
		functions.addAll(this.functions);
	}
	
	public void addFunction(FunctionDecl decl) {
		decl.setTypeDecl(this);
		
		if(!decl.isStatic()) {
			if(shouldAddThis(decl)) {
				Token tok = decl.getArguments().isEmpty() ? startToken : decl.getArguments().getFirst().startToken;
				decl.getArguments().add(0, new RegularArgument(getInstanceType(), "this",
						tok));
			}
		}
		
		if(decl.isSpecialFunc()) {
			FunctionDecl already = getFunction(decl.getName(), decl.getSuffix(), null);
			if(already != null) functions.remove(already);
		}
		functions.add(decl);
	}

	private boolean shouldAddThis(FunctionDecl decl) {
		if(decl.isStatic()) return false;
		return true;
	}
	
	public void getFunctionsRecursive(NodeList<FunctionDecl> functions) {
		for(FunctionDecl decl: functions) {
			boolean already = false;
			for(FunctionDecl decl2: functions) {
				if(decl.sameProto(decl2)) {
					already = true;
					break;
				}
			}
			if(!already) functions.add(decl);
		}
		if(superRef != null) superRef.getFunctionsRecursive(functions);
	}
	
	public VariableDecl getVariable(String name) {
		for(VariableDecl decl: variables) {
			if(decl.hasAtom(name)) return decl;
		}
		return null;
	}
	
	public FunctionDecl getNoargFunction(String name) {
		for(FunctionDecl decl: functions) {
			if(name.matches(decl.getName()) && decl.getArguments().size() == 1) return decl;
		}
		
		return null;
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		return this;
	}

	public String getVariablesRepr() {
		return variables.toString();
	}
	
	public String getFunctionsRepr() {
		return functions.toString();
	}

}
