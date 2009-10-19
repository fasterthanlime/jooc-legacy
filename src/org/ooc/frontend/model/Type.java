package org.ooc.frontend.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Type extends Node implements MustBeResolved {

	public String origin = null;
	
	public static class Classification {
		public static final int POINTER = 1;
		public static final int NUMBER = 2;
		public static final int CLASS = 4;
	}

	protected String name;
	protected int pointerLevel;
	protected int referenceLevel;
	private Declaration ref;
	
	private boolean isArray = false;
	private Expression arraySize = null;
	
	protected NodeList<Access> typeParams;
	private boolean isConst = false;
	
	private static Type voidType = null;
	
	public static Type getVoid() {
		if(voidType == null) {
			voidType = new Type("Void", Token.defaultToken);
		}
		return voidType;
	}
	
	public Type(String name, Token startToken) {
		this(name, 0, startToken);
	}
	
	public Type(String name, int pointerLevel, Token startToken) {
		this(name, pointerLevel, 0, startToken);
	}
	
	public Type(String name, int pointerLevel, int referenceLevel, Token startToken) {
		super(startToken);
		//FIXME debug
		StringWriter sw = new StringWriter();
		new Exception("Haha I'm created, biatches.").printStackTrace(new PrintWriter(sw));
		this.origin = sw.toString();
		this.name = name;
		this.pointerLevel = pointerLevel;
		this.referenceLevel = referenceLevel;
		this.typeParams = new NodeList<Access>(startToken);
	}
	
	public NodeList<Access> getTypeParams() {
		return typeParams;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPointerLevel(int pointerLevel) {
		this.pointerLevel = pointerLevel;
	}

	public int getPointerLevel() {
		return pointerLevel;
	}
	
	public void setReferenceLevel(int referenceLevel) {
		this.referenceLevel = referenceLevel;
	}
	
	public int getReferenceLevel() {
		return referenceLevel;
	}
	
	public Declaration getRef() {
		return ref;
	}
	
	public void setRef(Declaration ref) {
		this.ref = ref;
	}
	
	public boolean isConst() {
		return isConst;
	}
	
	public void setConst(boolean isConst) {
		this.isConst = isConst;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		if(arraySize != null) arraySize.accept(visitor);
		typeParams.accept(visitor);
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		if(isConst) sb.append("const ");
		sb.append(name);
		
		for(int i = 0; i < pointerLevel; i++) {
			if(isArray) sb.append("[]");
			else sb.append('*');
		}
		for(int i = 0; i < referenceLevel; i++) {
			sb.append('@');
		}
		if(!typeParams.isEmpty()) {
			sb.append('<');
			Iterator<Access> iter = typeParams.iterator();
			while(iter.hasNext()) {
				Access element = iter.next();
				if(element instanceof TypeAccess) {
					sb.append(element.toString());
				} else if(element instanceof VariableAccess) {
					sb.append(((VariableAccess) element).getName());
				} else if(element instanceof FunctionCall) {
					sb.append(((FunctionCall) element).getName());
				}
				if(iter.hasNext()) sb.append(", ");
			}
			sb.append('>');
		}
		
		return sb.toString();
		
	}
	
	public String getMangledName() {
		
		if(pointerLevel == 0) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for(int i = 0; i < pointerLevel + referenceLevel; i++) {
			sb.append("__star");
		}
		return sb.toString();
		
	}

	public boolean isVoid() {
		return (name.equals("void") || name.equals("Void")) && isFlat();
	}

	public boolean isFlat() {
		return pointerLevel == 0 && referenceLevel == 0 && !(ref instanceof ClassDecl);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == ref) {
			ref = (Declaration) kiddo;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Type) {
			Type type = (Type) obj;
			boolean result = name.equals(type.name) && pointerLevel == type.getPointerLevel();
			return result;
		}
		return super.equals(obj);
	}
	
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(name.equals("Func")) {
			FuncType newType = new FuncType(startToken);
			newType.setPointerLevel(pointerLevel);
			newType.setReferenceLevel(referenceLevel);
			newType.setArray(isArray);
			newType.setArraySize(arraySize);
			if(!stack.peek().replace(this, newType)) {
				Thread.dumpStack();
				throw new OocCompilationError(this, stack, "Couldn't replace "
						+this+" with "+newType+" in "+stack.peek());
			}
			return Response.OK;
		}
		
		if(ref != null) return Response.OK;
		
		ref = stack.getModule().getType(name);

		if(ref == null && name.equals("This")) {
			int index = stack.find(TypeDecl.class);
			if(index == -1) {
				throw new OocCompilationError(this, stack, "Using 'This' outside a type definition is meaningless.");
			}
			TypeDecl typeDecl = (TypeDecl) stack.get(index);
			name = typeDecl.getName();
			ref = typeDecl;
			return Response.OK;
		}
		
		if(ref == null) {
			TypeParam param = getTypeParam(stack, name);
			if(param != null) {
				ref = param;
				return Response.OK;
			}
		}
		
		if(ref == null && fatal) {
			if(res.params.veryVerbose) {
				Thread.dumpStack();
				throw new OocCompilationError(this, stack, "Couldn't resolve type "
					+getName()+", ref = "+ref+" at origin "+origin+" btw, stack = "+stack.toString(true));
			}
			throw new OocCompilationError(this, stack, "Couldn't resolve type "
					+getName());
		}
		
		return (ref == null) ? Response.LOOP : Response.OK;
		
	}

	public boolean isResolved() {
		return ref != null || name.length() == 0; // empty name = any type.
	}
	
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}
	
	public boolean isArray() {
		return isArray;
	}
	
	public Type getGroundType() {
		return getGroundType(null);
	}
	
	public Type getGroundType(Resolver res) {
		if(ref instanceof CoverDecl) {
			CoverDecl coverDecl = (CoverDecl) ref;
			if(coverDecl.getFromType() != null && !name.equals(coverDecl.getFromType().getName())) {
				Type rawType = coverDecl.getFromType().getGroundType(res);
				Type groundType = new Type(rawType.name, pointerLevel, referenceLevel, rawType.startToken);
				if(res == null) {
					groundType.ref = ref;
				} else {
					groundType.resolve(res);
				}
				return groundType;
			}
		}
		return this;
	}
	
	public Type getFlatType(Resolver res) {
		Type returnType = this;
		while(returnType.ref instanceof CoverDecl) {
			CoverDecl coverDecl = (CoverDecl) returnType.ref;
			Type fromType = coverDecl.getFromType();
			if(fromType == null) break;
			if(fromType.referenceLevel <= 0) break;
			
			returnType = new Type(fromType.name, fromType.pointerLevel - 1,
					returnType.referenceLevel - 1, fromType.startToken);
			returnType.resolve(res);
		}
		
		return returnType;
	}

	public boolean fitsIn(Type innerType) {
		if (equals(innerType)) return true;
		if (getClassification() == innerType.getClassification()) return true;
		return false;
	}
	
	public int getClassification() {
		if(!isFlat()) return Classification.POINTER;
		
		if(name.equals("Int")   || name.equals("UInt")  || name.equals("Short")
		|| name.equals("UShort")|| name.equals("Long")  || name.equals("ULong")
		|| name.equals("LLong") || name.equals("ULLong")|| name.equals("Char")
		|| name.equals("UChar") || name.equals("Int8")  || name.equals("Int16")
		|| name.equals("Int32") || name.equals("Int64") || name.equals("UInt8")
		|| name.equals("UInt16")|| name.equals("UInt32")|| name.equals("UInt64")
		|| name.equals("SizeT")
		) return Classification.NUMBER;
		
		return Classification.CLASS;
	}

	public void resolve(Resolver res) {
		ref = res.module.getType(name);
	}

	public boolean isSuperOf(Type type) {
		if(type == null) return false;
		if(this.equals(type)) return false;
		if(name.length() == 0 || type.name.length() == 0) return false;
		
		if(type.getRef() instanceof TypeDecl) {
			TypeDecl typeDecl = (TypeDecl) type.getRef();
			if(typeDecl.getSuperRef() != null) {
				Type superType = typeDecl.getSuperRef().getType();
				if(superType.getName().equals(this.getName())) {
					return true;
				}
				return isSuperOf(superType);
			}
		}
		return false;
	}

	public String getHierarchyRepr() {
		String repr = name;
		Type t = this;
		while(t.ref != null) {
			if(!(t.ref instanceof TypeDecl)) break;
			TypeDecl typeDecl = (TypeDecl) t.ref;
			if(typeDecl.getSuperRef() == null) break;
			t = typeDecl.getSuperRef().getType();
			repr += ":" + t;
		}
		return repr;
	}

	@Override
	public Type clone() {
		Type clone = new Type(name, pointerLevel, referenceLevel, startToken);
		clone.ref = ref;
		clone.isArray = isArray;
		clone.isConst = isConst;
		clone.typeParams.addAll(typeParams);
		return clone;
	}

	public boolean isGeneric() {
		return ref instanceof TypeParam;
	}
	
	public boolean isGenericRecursive() {
		return (ref instanceof TypeParam) || !typeParams.isEmpty();
	}

	public Expression getArraySize() {
		return arraySize;
	}

	public void setArraySize(Expression arraySize) {
		this.arraySize = arraySize;
	}

	public boolean softEquals(Type type, Resolver res) {
		if(type == null) return false;
		resolve(res);
		if(equals(type)) {
			return true;
		}
		Declaration ref = type.getRef();
		if(ref instanceof TypeDecl) {
			TypeDecl typeDecl = (TypeDecl) ref;
			if(typeDecl.getSuperType() != null) {
				Type subType = typeDecl.getSuperType();
				return softEquals(subType, res);
			}
		}
		
		if(		getClassification() == Classification.NUMBER
		&& type.getClassification() == Classification.NUMBER) return true;
		return false;
	}
	
	public boolean isPrefixed() {
	
		return (ref instanceof ClassDecl || (ref instanceof CoverDecl && !((CoverDecl) ref).isExtern()));
		
	}

	public String getUnderName() {
		
		if(isPrefixed()) return ((TypeDecl) ref).getUnderName();
		return getName();
		
	}

	public Type dereference() {
		Type clone = clone();
		clone.setPointerLevel(getPointerLevel() - 1);
		return clone;
	}
	
}

