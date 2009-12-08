package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;
import org.ubi.CompilationFailedError;

public class ClassDecl extends TypeDecl {

	public static final String DESTROY_FUNC_NAME = "__destroy__";
	public static final String DEFAULTS_FUNC_NAME = "__defaults__";
	public static final String LOAD_FUNC_NAME = "__load__";
	
	protected boolean hasRegisteredFinalizer = false;
	
	protected boolean isAbstract;
	
	protected OocDocComment comment;
	
	protected FunctionDecl defaultInit = null;
	
	public ClassDecl(String name, Type superType, boolean isAbstract, Module module, Token startToken) {
		super(name, (superType == null && !name.equals("Object")) ?
				new Type("Object", Token.defaultToken) : superType, module, startToken);
		this.isAbstract = isAbstract;
		
		addFunction(new FunctionDecl(LOAD_FUNC_NAME,     "", false, true,  false, false, startToken));
		addFunction(new FunctionDecl(DEFAULTS_FUNC_NAME, "", false, false, false, false, startToken));
		addFunction(new FunctionDecl(DESTROY_FUNC_NAME,  "", false, false, false, false, startToken));
	}

	public void addInit() {
		if(!isAbstract && defaultInit == null) {
			FunctionDecl init = new FunctionDecl("init", "", false, false, false, false, startToken);
			addFunction(init);
			defaultInit = init;
		}
	}
	
	@Override
	public ClassDecl getSuperRef() {
		TypeDecl ref = super.getSuperRef();
		if(ref != null && !(ref instanceof ClassDecl)) {
			throw new CompilationFailedError(null, "Huh your class '"+getName()
					+"' in '"+(module != null ? module.getFullName() : "<unknown module>")
					+"' extends "+ref.getName()+" which isn't a ClassDecl but a "
					+ref.getClass().getSimpleName());
		}
		return (ClassDecl) ref;
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
	public void addFunction(FunctionDecl decl) {
		
		if(decl.getName().equals("init")) {
			addInit(decl);
		} else if(decl.getName().equals("new")) {
			FunctionDecl already = getFunction(decl.getName(), decl.getSuffix(), null);
			if(already != null) { functions.remove(already); }
		}
		
		super.addFunction(decl);
	}

	private void addInit(FunctionDecl decl) {
		if(defaultInit != null) {
			FunctionDecl newFunc = getFunction("new", "", null);
			functions.remove(defaultInit);
			functions.remove(newFunc);
			defaultInit = null;
		}
		
		FunctionDecl constructor = new FunctionDecl("new", decl.getSuffix(), false, true, false, false, decl.startToken);
		Type retType = getType().clone();
		retType.getTypeParams().clear();
		
		constructor.getArguments().addAll(decl.getArguments());
		constructor.getTypeParams().putAll(getTypeParams());
		
		VariableAccess thisTypeAccess = new VariableAccess(name, decl.startToken);
		thisTypeAccess.setRef(this);
		VariableAccess classAccess = new MemberAccess(thisTypeAccess, "class", decl.startToken);
		MemberCall allocCall = new MemberCall(classAccess, "alloc", "", decl.startToken);
		Cast cast = new Cast(allocCall, getType(), decl.startToken);
		VariableDeclFromExpr vdfe = new VariableDeclFromExpr("this", cast, decl.startToken);
		constructor.getBody().add(new Line(vdfe));
		
		for(TypeParam genType: typeParams.values()) {
			VariableAccess e = new VariableAccess(genType.getName(), constructor.startToken);
			retType.getTypeParams().add(e);
			
			constructor.getBody().add(new Line(new Assignment(
					new MemberAccess(genType.getName(), startToken), e, constructor.startToken))
			);
		}
		constructor.setReturnType(retType);

		VariableAccess thisAccess = new VariableAccess(vdfe, decl.startToken);
		thisAccess.setRef(vdfe);
		
		FunctionCall initCall = new FunctionCall(decl, decl.startToken);
		for(Argument arg: constructor.getArguments()) {
			initCall.getArguments().add(new VariableAccess(arg, decl.startToken));
		}
		constructor.getBody().add(new Line(new MemberCall(thisAccess, initCall, decl.startToken)));
		constructor.getBody().add(new Line(new ValuedReturn(thisAccess, decl.startToken)));
		
		addFunction(constructor);
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public void getVariables(NodeList<VariableDecl> variables) {
		super.getVariables(variables);
		if(getSuperRef() != null) getSuperRef().getVariables(variables);
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;
		
		if(isResolved()) return Response.OK;
		
		if(getSuperType() != null && !(super.getSuperRef() instanceof ClassDecl)) {
			throw new OocCompilationError(this, stack, "Trying to extends a "
					+getSuperRef().getClass().getSimpleName()+". You can only extend classes.");
		}
		if (getSuperType() != null) {
			if(getSuperRef() == null) {
				if(fatal) throw new OocCompilationError(this, stack, "Super-type "
						+getSuperType()+" of class "+getType()+" couldn't be resolved");
				return Response.LOOP;
			}
			if(defaultInit == null) {
				FunctionDecl superInit = getSuperRef().getFunction("init", "", null);
				if(superInit != null && superInit == getSuperRef().defaultInit) {
					addInit();
					//return Response.RESTART;
				}
			}
		}
		
		if(!hasRegisteredFinalizer) {
			FunctionDecl finalizer = getFunction(DESTROY_FUNC_NAME, null, null);
			if(finalizer != null && !finalizer.getBody().isEmpty()) {
				FunctionDecl defaults = getFunction(DEFAULTS_FUNC_NAME, null, null);
				FunctionCall call = new FunctionCall("gc_register_finalizer", finalizer.startToken);
				NodeList<Expression> args = call.getArguments();
				/*
				 * void GC_debug_register_finalizer (GC_PTR obj, GC_finalization_proc fn, GC_PTR cd,
		  		 * GC_finalization_proc *ofn, GC_PTR *ocd);
				 */
				VariableAccess thisAccess = new VariableAccess("this", finalizer.startToken);
				args.add(thisAccess); // for object "this"
				
				VariableAccess destroyAccess = new MemberAccess(thisAccess, "__destroy__", finalizer.startToken);
				args.add(destroyAccess); // call the finalizer
				
				NullLiteral nil = new NullLiteral(finalizer.startToken);
				args.add(nil); // cd (no argument to apss)
				args.add(nil); // ofn (we don't care)
				args.add(nil); // ocd (we don't care)
				defaults.getBody().add(0, new Line(call));
				hasRegisteredFinalizer = true;
				return Response.LOOP;
			}
		}
		
		return Response.OK;
		
	}

	public ClassDecl getBaseClass(FunctionDecl decl) {
		if(getSuperRef() != null) {
			ClassDecl base = getSuperRef().getBaseClass(decl);
			if(base != null) return base;
		}
		if(getFunction(decl.getName(), decl.getSuffix(), null, false) != null) return this;
		return null;
	}
	
	public boolean isChildOf(String candidate) {
		if(getName().equals(candidate)) return true;
		if(getSuperRef() != null) return getSuperRef().isChildOf(candidate);
		return false;
	}
	
}
