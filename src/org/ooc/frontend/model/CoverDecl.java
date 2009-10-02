package org.ooc.frontend.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Compare.CompareType;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

/**
 * Covers can be defined several times, allowing to add functions, e.g.
 * you can add functions to the String cover from ooclib.ooc by redefining
 * a cover named String and adding your own functions.
 * 
 * The compiler handles it like this: it marks the 'redefined' cover as
 * an 'addon' of the original cover. Thus, its struct/typedef are not outputted,
 * only its functions. And, the addon 'absorbs' the original's functions, so
 * that everything is resolved properly.
 * 
 * @author Amos Wenger
 */
public class CoverDecl extends TypeDecl implements MustBeResolved {

	protected OocDocComment comment;
	protected Type fromType;
	protected CoverDecl base;
	protected List<CoverDecl> addons;
	protected FunctionDecl classGettingFunc;
	
	public CoverDecl(String name, Type superType, Type fromType, Module module, Token startToken) {
		super(name, superType, module, startToken);
		this.fromType = fromType;
		this.base = null;
		this.addons = new ArrayList<CoverDecl>();
		if(fromType != null) {
			instanceType.referenceLevel = fromType.referenceLevel;
		}
		if(fromType == null || (!fromType.isVoid())) {
			addClassDecl();
		}
	}

	private void addClassDecl() {
		classGettingFunc = new FunctionDecl("class", "", false, true, false, false, startToken);
		classGettingFunc.setReturnType(new Type("Class", startToken));
		
		FunctionCall classSizeOf = new FunctionCall("sizeof", startToken);
		classSizeOf.arguments.add(new VariableAccess("Class", startToken));
		
		FunctionCall malloc = new FunctionCall("gc_malloc", startToken);
		malloc.arguments.add(classSizeOf);
		
		FunctionCall coverSizeOf = new FunctionCall("sizeof", startToken);
		if(fromType == null || fromType.isFlat()) {
			coverSizeOf.arguments.add(new VariableAccess(getName(), startToken));
		} else {
			coverSizeOf.arguments.add(new VariableAccess("Pointer", startToken));
		}
		
		VariableDecl varDecl = new VariableDecl(new Type("Class", startToken), true, startToken);
		NullLiteral nullLiteral = new NullLiteral(startToken);
		varDecl.atoms.add(new VariableDeclAtom("_class", nullLiteral, startToken));
		classGettingFunc.getBody().add(new Line(varDecl));
		
		VariableAccess classAccess = new VariableAccess("_class", startToken);
		
		If ifNull = new If(new Compare(classAccess, nullLiteral, CompareType.EQUAL, startToken), startToken);
		classGettingFunc.getBody().add(new Line(ifNull));
		
		ifNull.body.add(new Line(new Assignment(classAccess, malloc, startToken)));
		ifNull.body.add(new Line(new Assignment(new MemberAccess(classAccess, "size", startToken),
				coverSizeOf, startToken)));
		ifNull.body.add(new Line(new Assignment(new MemberAccess(classAccess, "instanceSize", startToken),
				coverSizeOf, startToken)));
		ifNull.body.add(new Line(new Assignment(new MemberAccess(classAccess, "name", startToken),
				new StringLiteral(name, startToken), startToken)));
		
		classGettingFunc.getBody().add(new Line(new ValuedReturn(classAccess, startToken)));

		addFunction(classGettingFunc);
	}
	
	@Override
	public void addFunction(FunctionDecl decl) {
		super.addFunction(decl);
	}
	
	@Override
	public FunctionDecl getFunction(String name, String suffix,
			FunctionCall call, boolean recursive, int bestScoreParam, FunctionDecl bestMatchParam) {
		return getFunction(name, suffix, call, recursive, new HashSet<CoverDecl>(), bestScoreParam, bestMatchParam);
	}

	private FunctionDecl getFunction(String name, String suffix,
			FunctionCall call, boolean recursive, HashSet<CoverDecl> done,
			int bestScoreParam, FunctionDecl bestMatchParam) {
		
		int bestScore = bestScoreParam;
		FunctionDecl bestMatch = bestMatchParam;
		FunctionDecl function = super.getFunction(name, suffix, call, recursive, bestScore, bestMatch);
		if(function != null) {
			if(call == null) return function;
			int score = call.getScore(function);
			if(score > bestScore) {
				bestScore = score;
				bestMatch = function;
			}
		}
		
		for(CoverDecl addon: addons) {
			if(done.contains(addon)) continue;
			done.add(addon);
			function = addon.getFunction(name, suffix, call, recursive, done, bestScore, bestMatch);
			if(function != null) {
				if(call == null) return function;
				int score = call.getScore(function);
				if(score > bestScore) {
					bestScore = score;
					bestMatch = function;
				}
			}
		}
		if(base != null) {
			if(!done.contains(base)) {
				done.add(base);
				function = base.getFunction(name, suffix, call, recursive, done, bestScore, bestMatch);
				if(function != null) {
					if(call == null) return function;
					int score = call.getScore(function);
					if(score > bestScore) {
						bestScore = score;
						bestMatch = function;
					}
				}
			}
		}
		if(bestMatch == null && getSuperRef() != null) {
			return getSuperRef().getFunction(name, suffix, call, recursive, 0, null);
		}
		return bestMatch;
		
	}

	public Type getFromType() {
		return fromType;
	}
	
	public OocDocComment getComment() {
		return comment;
	}
	
	public void setComment(OocDocComment comment) {
		this.comment = comment;
	}
	
	public boolean isAddon() {
		return base != null;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		if(fromType != null) fromType.accept(visitor);
		super.acceptChildren(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == instanceType) {
			instanceType = (Type) kiddo;
			return true;
		}
		
		if(oldie == fromType) {
			fromType = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	public void absorb(CoverDecl node) {
		assert(variables.isEmpty());
		System.out.println(this+":"+module.getPackageName()+" absorbed "+node+":"+node.module.getPackageName());
		base = node;
		base.addAddon(this);
		if(classGettingFunc != null) functions.remove(classGettingFunc);
	}

	private void addAddon(CoverDecl node) {
		System.out.println(this+":"+module.getPackageName()+" got addon "+node+":"+node.module.getPackageName());
		addons.add(node);
	}

	public boolean isResolved() {
		return (fromType == null || fromType.getRef() != null);
	}

	/**
	 * There's a trick about CoverDecl.
	 * If the fromType is defined somewhere (e.g. if it's another cover),
	 * then it must be ref'd correctly.
	 * If it's not, then a {@link BuiltinType} must be created
	 * so that it's considered 'resolved' (e.g. it's somewhere in C)
	 */
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(fromType == null) return Response.OK;
		fromType.resolve(res);
		
		if(fromType.getRef() == null) {
			fromType.setRef(new BuiltinType(fromType));
		}

		return Response.OK;
		
	}

}
