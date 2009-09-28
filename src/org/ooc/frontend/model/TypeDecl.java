package org.ooc.frontend.model;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;


public abstract class TypeDecl extends Declaration implements Scope, Generic {

	protected NodeList<VariableDecl> variables;
	protected NodeList<FunctionDecl> functions;
	
	protected Type superType;
	
	protected Type instanceType;
	protected LinkedHashMap<String, TypeParam> typeParams;
	
	protected Module module;
	
	public TypeDecl(String name, Type superType, Module module, Token startToken) {
		super(name, startToken);
		this.module = module;
		this.superType = superType;
		this.variables = new NodeList<VariableDecl>(startToken);
		this.functions = new NodeList<FunctionDecl>(startToken);
		this.instanceType = new Type(name, startToken);
		instanceType.setRef(this);
		this.typeParams = new LinkedHashMap<String, TypeParam>();
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
	
	public Type getSuperType() {
		return superType;
	}
	
	public String getSuperName() {
		return superType == null ? "" : superType.getName();
	}
	
	public void setSuperType(Type superType) {
		this.superType = superType;
	}
	
	public FunctionDecl getFunction(FunctionCall call) {
		return getFunction(call.getName(), call.getSuffix(), call);
	}
	
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call, boolean recursive) {
		return getFunction(name, suffix,call, recursive, 0, null);
	}
	
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call,
			boolean recursive, int bestScoreParam, FunctionDecl bestMatchParam) {
		int bestScore = bestScoreParam;
		FunctionDecl bestMatch = bestMatchParam;
		for(FunctionDecl func : functions) {
			if(func.getName().equals(name) && (suffix == null || func.getSuffix().equals(suffix))) {
				if(call == null) return func;
				int score = call.getScore(func);
				if(score == -1) return null;
				if(score > bestScore) {
					bestScore = score;
					bestMatch = func;
				}
			}
		}
		if(recursive && getSuperRef() != null) return getSuperRef().getFunction(name, suffix, call, true, bestScore, bestMatch);
		return bestMatch;
	}
	
	public TypeDecl getSuperRef() {
		if(superType == null || superType.getRef() == null) return null;
		return (TypeDecl) superType.getRef();
	}

	public FunctionDecl getFunction(String name, String suffix, FunctionCall call) {
		return getFunction(name, suffix, call, true, 0, null);
	}
	
	public void getFunctions(NodeList<FunctionDecl> functions) {
		functions.addAll(this.functions);
	}
	
	public void addFunction(FunctionDecl decl) {
		decl.setTypeDecl(this);
		
		if(!decl.isStatic()) {
			Token tok = decl.getArguments().isEmpty() ? startToken : decl.getArguments().getFirst().startToken;
			decl.getArguments().add(0, new RegularArgument(getInstanceType(), "this", tok));
		} else {
			// static functions must have the same type params as the class
			decl.getTypeParams().putAll(typeParams);
		}
		
		if(decl.isSpecialFunc()) {
			FunctionDecl already = getFunction(decl.getName(), decl.getSuffix(), null);
			if(already != null) functions.remove(already);
		}
		functions.add(decl);
	}
	
	public void getFunctionsRecursive(NodeList<FunctionDecl> functions) {
		for(FunctionDecl decl: this.functions) {
			boolean already = false;
			for(FunctionDecl decl2: functions) {
				if(decl != decl2 && decl.sameProto(decl2)) {
					already = true;
					break;
				}
			}
			if(!already) functions.add(decl);
		}
		if(getSuperRef() != null) getSuperRef().getFunctionsRecursive(functions);
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
	
	public void acceptChildren(Visitor visitor) throws IOException {
		if(superType != null) superType.accept(visitor);
		for(TypeParam genType: typeParams.values()) {
			genType.accept(visitor);
		}
		variables.accept(visitor);
		functions.accept(visitor);
		instanceType.accept(visitor);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		return this;
	}
	
	public Type getType() {
		return getInstanceType();
	}

	public String getVariablesRepr() {
		return variables.toString();
	}
	
	public String getFunctionsRepr() {
		return functions.toString();
	}
	
	public LinkedHashMap<String, TypeParam> getTypeParams() {
		return typeParams;
	}
	
	public void addTypeParam(TypeParam genType) {
		typeParams.put(genType.name, genType);
		genType.getArgument().setTypeDecl(this);
		variables.add(0, genType.getArgument());
		
		instanceType.getTypeParams().add(new VariableAccess(genType.getName(), genType.startToken));
	}
	
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder(getClass().getSimpleName());
		sB.append(' ');
		sB.append(name);
		if(!typeParams.isEmpty()) {
			sB.append('<');
			boolean isFirst = true;
			for(String typeParam: typeParams.keySet()) {
				if(!isFirst) sB.append(", ");
				sB.append(typeParam);
			}
			sB.append('>');
		}
		
		return sB.toString();
	}
	
	public String getUnderName() {
		if(module != null && module.getPackageName().length() > 0 && !isExtern())
			return module.getPackageName() + "__" + getName();
		return getName();
	}
	
	public Module getModule() {
		return module;
	}

}
