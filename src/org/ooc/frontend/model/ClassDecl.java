package org.ooc.frontend.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ClassDecl extends TypeDecl implements MustBeResolved, Generic {

	protected boolean isAbstract;
	
	protected OocDocComment comment;

	protected FunctionDecl initialize;
	protected FunctionDecl load;
	protected FunctionDecl defaultConstructor;

	protected List<TypeParam> typeParams;
	
	public ClassDecl(String name, String superName, boolean isAbstract, Token startToken) {
		super(name, (superName.isEmpty() && !name.equals("Object")) ? "Object" : superName, startToken);
		this.isAbstract = isAbstract;
		this.initialize = new FunctionDecl("initialize", "", false, false, false, false, startToken);
		this.initialize.getArguments().add(new RegularArgument(instanceType, "this", startToken));
		this.initialize.setTypeDecl(this);
		this.load = new FunctionDecl("load", "", false, false, false, false, startToken);
		this.load.setStatic(true);
		this.load.setTypeDecl(this);
		this.superRef = null;
		FunctionDecl constructor = new FunctionDecl("new", "", false, false, false, false, startToken);
		addFunction(constructor);
		this.defaultConstructor = constructor;
		this.typeParams = new ArrayList<TypeParam>();
	}
	
	@Override
	public ClassDecl getSuperRef() {
		return (ClassDecl) superRef;
	}

	public boolean isObjectClass() {
		return name.equals("Object");
	}
	
	public boolean isClassClass() {
		return name.equals("Class");
	}
	
	public boolean isRootClass() {
		return isObjectClass() || isClassClass();
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public FunctionDecl getInitializeFunc() {
		return initialize;
	}
	
	public FunctionDecl getLoadFunc() {
		return load;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	@Override
	public void addFunction(FunctionDecl decl) {
		if(defaultConstructor != null && decl.isConstructor()) {
			functions.remove(defaultConstructor);
			defaultConstructor = null;
		}
		super.addFunction(decl);
	}

	@Override
	public Type getType() {
		return getInstanceType();
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		variables.accept(visitor);
		functions.accept(visitor);
		initialize.accept(visitor);
		load.accept(visitor);
		instanceType.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	@Override
	public VariableDecl getVariable(String name) {
		VariableDecl variable = super.getVariable(name);
		if(variable != null) return variable;
		if(superRef != null) return superRef.getVariable(name);
		return null;
	}
	
	@Override
	public void getVariables(NodeList<VariableDecl> variables) {
		super.getVariables(variables);
		if(superRef != null) superRef.getVariables(variables);
	}

	@Override
	public boolean isResolved() {
		return superName.isEmpty() || superRef != null;
	}

	@Override
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		if(isResolved()) return false;
		if(!(superRef instanceof ClassDecl)) {
			throw new OocCompilationError(this, stack, "Trying to extends a "
					+superRef.getClass().getSimpleName()+". You can only extend classes.");
		}
		return superRef == null;
		
	}

	@Override
	public List<TypeParam> getTypeParams() {
		return typeParams;
	}
	
}
