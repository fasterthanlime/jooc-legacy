package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.NodeList.AddListener;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class FunctionDecl extends Declaration implements Scope, Generic, MustBeUnwrapped, PotentiallyStatic {

	public static Type type = new FuncType(Token.defaultToken);
	
	protected OocDocComment comment;
	
	protected boolean isFinal;
	protected boolean isStatic;
	protected boolean isAbstract;
	protected boolean isProto = false;
	protected boolean isInline = false;
	protected boolean fromPointer = false;
	
	protected TypeDecl typeDecl;

	protected String suffix;
	private final NodeList<Line> body;
	
	protected Type returnType;
	// when the return type is generic, the returnArg is a pointer.
	protected Argument returnArg;
	
	protected final LinkedHashMap<String, TypeParam> typeParams;
	private final NodeList<Argument> arguments;
	
	public FunctionDecl(String name, String suffix, boolean isFinal,
			boolean isStatic, boolean isAbstract, boolean isExtern, Token startToken) {
		this(name, suffix, isFinal, isStatic, isAbstract, isExtern ? "" : null, startToken);
	}
	
	public FunctionDecl(String name, String suffix, boolean isFinal,
			boolean isStatic, boolean isAbstract, String externName, Token startToken) {
		super(name, externName, startToken);
		this.suffix = suffix;
		this.isFinal = isFinal;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.body = new NodeList<Line>(startToken);
		this.returnType = name.equals("main") ? IntLiteral.type : Type.getVoid();
		this.arguments = new NodeList<Argument>(startToken);
		this.arguments.addAddListener(new AddListener<Argument>() {
			public void onAdd(NodeList<Argument> list, Argument arg) {
				TypeParam typeParam = typeParams.get(arg.getName());
				if(typeParam != null) {
					typeParam.setGhost(true);
				}
			}
		});
		this.typeParams = new LinkedHashMap<String, TypeParam>();
		// FIXME this will bite us in the ass later. Ohh yes it will
		this.returnArg = new RegularArgument(NullLiteral.type, "__returnArg", startToken);
	}

	public LinkedHashMap<String, TypeParam> getTypeParams() {
		return typeParams;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public boolean isFromPointer() {
		return fromPointer;
	}
	
	public void setFromPointer(boolean fromPointer) {
		this.fromPointer = fromPointer;
	}
	
	public boolean isProto() {
		return isProto;
	}
	
	public void setProto(boolean isProto) {
		this.isProto = isProto;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public boolean isFinal() {
		return isFinal;
	}
	
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		return typeDecl;
	}
	
	public boolean isInline() {
		return isInline;
	}
	
	public void setInline(boolean isInline) {
		this.isInline = isInline;
	}
	
	public void setTypeDecl(TypeDecl typeDecl) {
		this.typeDecl = typeDecl;
	}
	
	/**
	 * @return true if it's a member function
	 */
	public boolean isMember() {
		return typeDecl != null;
	}
	
	public boolean hasThis() {
		return !isStatic() && isMember();
	}
	
	public NodeList<Line> getBody() {
		return body;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}
	
	public NodeList<Argument> getArguments() {
		return arguments;
	}

	public Type getType() {
		return type;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		if (typeParams.size() > 0) for (TypeParam typeParam: typeParams.values()) {
			typeParam.getType().accept(visitor);
		}
		arguments.accept(visitor);
		returnType.accept(visitor);
		returnArg.getType().accept(visitor);
		body.accept(visitor);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		
		if(oldie == returnType) {
			returnType = (Type) kiddo;
			return true;
		}
		
		return false;
		
	}

	public String getArgsRepr() {
		return getArgsRepr(true);
	}	
	
	public String getArgsRepr(boolean skipThis) {
		
		StringBuilder sB = new StringBuilder();
		sB.append('(');
		Iterator<Argument> iter = arguments.iterator();
		if(skipThis && hasThis()) iter.next();
		while(iter.hasNext()) {
			Argument arg = iter.next();
			if(arg instanceof VarArg) sB.append("...");
			else sB.append(arg.getType());
			
			if(iter.hasNext()) sB.append(", ");
		}
		sB.append(')');
		
		return sB.toString();
		
	}
	
	@Override
	public String toString() {
		
		String name = isMember() ? typeDecl.getType() + "." + getSuffixedName() : getSuffixedName();
		String repr = /*getClass().getSimpleName()+" : "+*/name+getArgsRepr();
		return repr;
		
	}

	public String getFullName() {
		
		StringBuilder sB = new StringBuilder();
		try {
			writeFullName(sB);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sB.toString();
		
	}

	public void writeFullName(Appendable dst) throws IOException {
		
		if(externName != null && externName.length() > 0) {
			dst.append(externName);
		} else {
			if(isMember()) {
				dst.append(typeDecl.getExternName()).append('_');
			}
			writeSuffixedName(dst);
		}
		
	}

	public void writeSuffixedName(Appendable dst) throws IOException {
		
		dst.append(getExternName());
		if(suffix.length() > 0) {
			dst.append('_').append(suffix);
		}
		
	}

	public String getProtoRepr() {
		return getProtoRepr(false);
	}
	
	public String getProtoRepr(boolean skipThis) {
		if(typeDecl != null) return typeDecl.getName()+"."+name+getArgsRepr(skipThis);
		return name+getArgsRepr(skipThis);
	}

	public boolean sameProto(FunctionDecl decl2) {
		return name.equals(decl2.getName()) && (suffix.equals(decl2.getSuffix()));
	}

	public boolean isEntryPoint() {
		return name.equals("main");
	}

	public VariableDecl getVariable(String name) {
		if(arguments.size() > 0) for(Argument argument: arguments) {
			if(argument.hasAtom(name)) return argument;
		}
		return getVariable(body, name);
	}

	public void getVariables(NodeList<VariableDecl> variables) {
		if(arguments.size() > 0) for(Argument argument: arguments) {
			variables.add(argument);
		}
		getVariables(body, variables);
	}
	
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call) {
		return null;
	}

	public void getFunctions(NodeList<FunctionDecl> functions) {}

	public String getSuffixedName() {
		if(suffix.length() == 0) return name;
		return name+"_"+suffix;
	}

	public boolean unwrap(NodeList<Node> stack) throws IOException {
		if(name.length() == 0) {
			Module module = stack.getModule();
			name = stack.get(0).generateTempName(module.getUnderName()+"_closure", stack);
			VariableAccess varAcc = new VariableAccess(name, startToken);
			varAcc.setRef(this);
			stack.peek().replace(this, varAcc);
			module.getBody().add(this);
			
			return true;
		}
		
		if(isEntryPoint()) {
			if(arguments.size() == 1 && arguments.getFirst().getType().getName().equals("Array")) {
				Argument arg = arguments.getFirst();
				arguments.clear();
				Argument argc = new RegularArgument(IntLiteral.type, "argc", arg.startToken);
				Argument argv = new RegularArgument(new Type("String", 1, arg.startToken), "argv", arg.startToken);
				arguments.add(argc);
				arguments.add(argv);
				
				MemberCall constructCall = new MemberCall(new TypeAccess(arg.getType()), "new", "withData", arg.startToken);
				constructCall.getTypeParams().add(new TypeAccess(NullLiteral.type));
				constructCall.getArguments().add(new VariableAccess(argv, startToken));
				constructCall.getArguments().add(new VariableAccess(argc, startToken));
				
				VariableDeclFromExpr vdfe = new VariableDeclFromExpr(arg.getName(), 
						constructCall, arg.startToken);
				
				body.add(0, new Line(vdfe));
			}
		}
		
		return false;
	}

	public boolean isExternWithName() {
		return externName != null && externName.length() > 0;
	}

	public Argument getReturnArg() {
		return returnArg;
	}

	public boolean isNamed(String name, String suffix) {
		return this.name.equals(name) && (suffix == null || this.suffix.equals(suffix));
	}

	public boolean isSpecialFunc() {
		return name.equals(ClassDecl.DEFAULTS_FUNC_NAME)
			|| name.equals(ClassDecl.DESTROY_FUNC_NAME)
			|| name.equals(ClassDecl.LOAD_FUNC_NAME);
	}

	public Iterator<Argument> getThisLessArgsIter() {
		Iterator<Argument> iter = getArguments().iterator();
		if(hasThis()) iter.next();
		return iter;
	}

	public boolean hasReturn() {
		return !getReturnType().isVoid() && !(getReturnType().getRef() instanceof TypeParam);
	}

	public TypeParam getGenericType(String name) {
		TypeParam genericType = typeParams.get(name);
		if(genericType == null && typeDecl != null) {
			Map<String, TypeParam> classGenerics = typeDecl.getTypeParams();
			genericType = classGenerics.get(name);
			return genericType;
		}
		return genericType;
	}

	public boolean isGeneric() {
		if(typeParams.size() > 0) return true;
		if(typeDecl != null && typeDecl.getTypeParams().size() > 0) return true;
		return false;
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;
		
		if(isMember() && typeDecl.getSuperRef() != null) {
			FunctionDecl sup = typeDecl.getSuperRef().getFunction(name, suffix, null);
			if(sup != null && (sup.getArguments().size() != getArguments().size())) {
				if(name.equals("new") || name.equals("init")) {
					throw new OocCompilationError(this, stack, "There's not a no-argument constructor in super-type "
							+typeDecl.getSuperRef().getName()+", you should add a constructor to "
							+typeDecl.getName()+" with a suffix, e.g. init: func ~mySuffix () {}");
				}
				throw new OocCompilationError(this, stack, "Definition of "
						+this+" conflicts with definition in super-type "
						+typeDecl.getSuperRef().getName()+", you should add a suffix to this one or make it have the same arguments.");
			}
		}
		
		if(!getReturnType().isVoid() && !isExtern() && !isAbstract()) {
			
			if(getBody().isEmpty()) {
				if(getName().equals("main")) {
					getBody().add(new Line(new ValuedReturn(
							new IntLiteral(0, Format.DEC, startToken), startToken)));
					return Response.RESTART;
				} /*else {
					
					throw new OocCompilationError(node, stack,
							"Returning nothing in function "+getProtoRepr()
								+" that should return a "+getReturnType());
					
				}*/
			} else {
				
				Line line = getBody().getLast();
				if(!(line.getStatement() instanceof Return)) {
					if(isEntryPoint()) {
						getBody().add(new Line(new ValuedReturn(
								new IntLiteral(0, Format.DEC, startToken), startToken)));
						return Response.RESTART;
					} else if(line.getStatement() instanceof Expression) {
						line.setStatement(new ValuedReturn((Expression) line.getStatement(),
								line.getStatement().startToken));
						return Response.RESTART;
					} /*else {
						
						throw new OocCompilationError(node, stack,
								"Returning nothing in function "+getProtoRepr()
									+" that should return a "+getReturnType());
					}*/
				}
			
			}
		}
		
		return Response.OK;
	}
	
}
