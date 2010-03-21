package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.Versioned;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;


public abstract class TypeDecl extends Declaration implements Scope, Generic, Versioned {

	private VersionBlock version = null;
	
	protected NodeList<VariableDecl> variables;
	protected NodeList<FunctionDecl> functions;
	
	protected Type superType;
	
	protected Type instanceType;
	protected LinkedHashMap<String, TypeParam> typeParams;
	
	protected VariableDecl thisDecl;
	
	public TypeDecl(String name, Type superType, Module module, Token startToken) {
		super(name, startToken, module);
		this.superType = superType;
		this.variables = new NodeList<VariableDecl>(startToken);
		this.functions = new NodeList<FunctionDecl>(startToken);
		this.instanceType = new Type(name, startToken);
		instanceType.setRef(this);
		this.typeParams = new LinkedHashMap<String, TypeParam>();
		this.thisDecl = new VariableDecl(instanceType, "this", startToken, module);
	}
	
	public VariableDecl getThisDecl() {
		return thisDecl;
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
		String realTypeParam = translateTypeParam(name);
		if(debugCondition()) {
			System.out.println("Looking for variable '"+name+"' in "+this);
		}
		
		if(realTypeParam != null) {
			System.out.println("KALAMAZOO, "+name+" => "+realTypeParam+" in type "+this);
			return getVariable(realTypeParam);
		}
		
		for(VariableDecl decl: variables) {
			if(decl.getName().equals(name)) {
				return decl;
			}
		}
		if(getSuperRef() != null) return getSuperRef().getVariable(name);
		return null;
	}
	
	private boolean debugCondition() {
		return this.name.equals("HashMap");
	}

	private String translateTypeParam(String name) {
		
		// Iterator: class <T>
		// HashMap: class <K, V> extends Iterator<V>
		// V needs to be written at T
		// If needle is contained in our typeParams, we need to figure out where it's used
		if(typeParams.containsKey(name)) {
			String result = null;
			
			Iterator<Access> iter1 = getSuperType().getTypeParams().iterator();
			Iterator<String> iter2 = getSuperRef().getTypeParams().keySet().iterator();
			while(iter1.hasNext()) {
				Access a = iter1.next();
				String candidate = iter2.next();
				if(a instanceof VariableAccess) {
					VariableAccess va = (VariableAccess) a;
					if(va.getName().equals(name) && !name.equals(candidate)) {
						result = candidate;
						break;
					}
				}
			}
			
			return result;
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
		typeParams.put(genType.getName(), genType);
		genType.getArgument().setTypeDecl(this);
		instanceType.getTypeParams().add(new VariableAccess(genType.getName(), genType.startToken));
		variables.add(0, genType.getArgument());
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		stack.push(this);
		
		// remove ghost type arguments
        if(superType != null) {
            Response response = superType.resolve(stack, res, fatal);
            if(response != Response.OK) {
                stack.pop(this);
                return response;
            }
            
            Type sType = this.superType;
            while(sType != null) {
                TypeDecl sTypeRef = (TypeDecl) sType.getRef();
                if(sTypeRef == null) {
                	// Need super type ref
                	stack.pop(this);
                	return Response.LOOP;
                }
                for(TypeParam typeArg: typeParams.values()) {
                    for(TypeParam candidate: sTypeRef.getTypeParams().values()) {
                        if(typeArg.getName().equals(candidate.getName())) {
                        	//System.out.println("[KALAMAZOO] Removing duplicate typeArg "+typeArg.getName()+" between "+this+" and "+sTypeRef);
                            variables.remove(getVariable(typeArg.getName()));
                        }
                    }
                }
                sType = sTypeRef.superType;
            }
        }
        
        stack.pop(this);
		
		return super.resolve(stack, res, fatal);
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
				if(isFirst) isFirst = false;
				else        sB.append(", ");
				sB.append(typeParam);
			}
			sB.append('>');
		}
		
		return sB.toString();
	}
	
	public String getUnderName() {
		if(module != null && !isExtern())
			return module.getMemberPrefix() + getName();
		return getName();
	}
	
	public Module getModule() {
		return module;
	}
	
	public VersionBlock getVersion() {
		return version;
	}
	
	public void setVersion(VersionBlock block) {
		this.version = block;
	}

	@Override
	public void addToModule(Module module) {
		module.addType(this);
	}

}
