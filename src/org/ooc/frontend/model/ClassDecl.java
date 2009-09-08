package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ClassDecl extends TypeDecl implements MustBeResolved {

	protected boolean isAbstract;
	
	protected OocDocComment comment;
	
	protected FunctionDecl defaultInit = null;
	
	public ClassDecl(String name, String superName, boolean isAbstract, Token startToken) {
		super(name, (superName.isEmpty() && !name.equals("Object")) ? "Object" : superName, startToken);
		this.isAbstract = isAbstract;
		this.superRef = null;
		
		addFunction(new FunctionDecl("load",     "", false, true,  false, false, startToken));
		addFunction(new FunctionDecl("defaults", "", false, false, false, false, startToken));
		addFunction(new FunctionDecl("destroy",  "", false, false, false, false, startToken));
		if(!isAbstract) {
			FunctionDecl init = new FunctionDecl("init",     "", false, false, false, false, startToken);
			addFunction(init);
			defaultInit = init;
		}
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
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	@Override
	public Type getType() {
		return getInstanceType();
	}
	
	@Override
	public void addFunction(FunctionDecl decl) {
		
		if(decl.getName().equals("init")) {
			if(defaultInit != null) {
				FunctionDecl newFunc = getFunction("new", "", null);
				functions.remove(defaultInit);
				functions.remove(newFunc);
				defaultInit = null;
			}
			
			FunctionDecl constructor = new FunctionDecl("new", decl.getSuffix(), false, true, false, false, decl.startToken);
			Type retType = getType().clone();
			for(GenericType genType: genericTypes.values()) {
				Type e = new Type(genType.getName(), genType.startToken);
				e.setRef(genType);
				retType.getGenericTypes().add(e);
			}
			constructor.setReturnType(retType);
			constructor.arguments.addAll(decl.getArguments());
			
			VariableAccess thisTypeAccess = new VariableAccess(name, decl.startToken);
			thisTypeAccess.setRef(this);
			VariableAccess classAccess = new MemberAccess(thisTypeAccess, "class", decl.startToken);
			MemberCall allocCall = new MemberCall(classAccess, "alloc", "", decl.startToken);
			Cast cast = new Cast(allocCall, getType(), decl.startToken);
			VariableDeclFromExpr vdfe = new VariableDeclFromExpr("this", cast, decl.startToken);
			constructor.body.add(new Line(vdfe));

			VariableAccess thisAccess = new VariableAccess(vdfe, decl.startToken);
			thisAccess.setRef(vdfe);
			
			FunctionCall initCall = new FunctionCall(decl, decl.startToken);
			for(Argument arg: constructor.getArguments()) {
				initCall.getArguments().add(new VariableAccess(arg, decl.startToken));
			}
			constructor.body.add(new Line(new MemberCall(thisAccess, initCall, decl.startToken)));
			constructor.body.add(new Line(new ValuedReturn(thisAccess, decl.startToken)));
			
			addFunction(constructor);
		}
		
		super.addFunction(decl);
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
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal)
			throws IOException {
		
		if(isResolved()) return Response.OK;
		if(!(superRef instanceof ClassDecl)) {
			throw new OocCompilationError(this, stack, "Trying to extends a "
					+superRef.getClass().getSimpleName()+". You can only extend classes.");
		}
		return (superRef == null) ? Response.LOOP : Response.OK;
		
	}

	public ClassDecl getBaseClass(FunctionDecl decl) {
		if(superRef != null) {
			ClassDecl base = ((ClassDecl) superRef).getBaseClass(decl);
			if(base != null) return base;
		}
		if(getFunction(decl.getName(), decl.getSuffix(), null) != null) return this;
		return null;
	}
	
}
