package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class VariableAccess extends Access implements MustBeResolved {

	/// dead means that it won't be resolved anymore
	protected boolean dead = false;
	
	private String name;
	protected Declaration ref;
	
	public VariableAccess(String variable, Token startToken) {
		super(startToken);
		this.name = variable;
	}

	public VariableAccess(VariableDecl varDecl, Token startToken) {
		super(startToken);
		assert(varDecl.atoms.size() == 1);
		this.name = varDecl.getName();
		ref = varDecl;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Declaration getRef() {
		return ref;
	}
	
	public void setRef(Declaration ref) {
		this.ref = ref;
	}

	public Type getType() {
		if(ref != null) {
			return ref.getType();
		}
		return null;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == ref) {
			ref = (VariableDecl) kiddo;
			return true;
		}
		return false;
	}

	public boolean isResolved() {
		return ref != null;
	}

	public Response resolve(final NodeList<Node> stack, final Resolver res, final boolean fatal) {
		
		if(isResolved()) return Response.OK;

		// Look if it is a namespace.
		{
			// But only if we don't already have a Namespace.
			if(!(stack.peek() instanceof NamespaceDecl)) {
				NamespaceDecl ns = stack.getModule().getNamespace(getName());
				if(ns != null) {
					// Yes, it is.
					ref = ns;
					return Response.OK;
				}
			}
		}
		
		// Search in the type params of the current function
		{
			int funcIndex = stack.find(FunctionDecl.class);
			if(funcIndex != -1) {
				FunctionDecl decl = (FunctionDecl) stack.get(funcIndex);
				TypeParam genType = decl.getTypeParams().get(name);
				if(genType != null) {
					if(ref == null) {
						ref = genType.getArgument();
					}
					return Response.OK;
				}
			}
		}
		
		{
			VariableDecl varDecl = getVariable(name, stack, this);
			if(varDecl != null) {
				if(varDecl.isMember()) {
					VariableAccess thisAccess = new VariableAccess("this", startToken);
					MemberAccess membAcc =  new MemberAccess(thisAccess, name, startToken);
					membAcc.resolve(stack, res, fatal);
					if(!stack.peek().replace(VariableAccess.this, membAcc)) {
						throw new Error("Couldn't replace a VariableAccess with a MemberAccess! stack = "+stack.toString(true));
					}
					//return Response.RESTART;
					return Response.LOOP;
				}
				ref = varDecl;
				return Response.OK;
			}
		}
		
		if(name.equals("this")) {
			int index = stack.find(TypeDecl.class);
			if(index != -1) {
				TypeDecl typeDecl = (TypeDecl) stack.get(index);
				ref =  typeDecl.getThisDecl();
				return Response.OK;
			}
		}
		
		{
			FunctionDecl func = getFunction(name, null, null, stack);
			if(func != null) {
				ref = func;
				return Response.OK;
			}
			for(Import imp: stack.getModule().getGlobalImports()) {
				func = imp.getModule().getFunction(name, null, null);
				if(func != null) {
					ref = func;
					return Response.OK;
				}
			}
		}
		
		if(ref != null) return Response.OK;
		int typeIndex = stack.find(TypeDecl.class);
		if(typeIndex != -1) {
			TypeDecl typeDecl = (TypeDecl) stack.get(typeIndex);
			if(name.equals("This")) {
				name = typeDecl.getName();
				ref = typeDecl;
				//return Response.RESTART;
			}
			VariableDecl varDecl = typeDecl.getVariable(name, null);
			if(varDecl != null) {
				VariableAccess thisAccess = new VariableAccess("this", startToken);
				thisAccess.setRef(varDecl);
				MemberAccess membAccess = new MemberAccess(thisAccess, name, startToken);
				membAccess.setRef(varDecl);
				if(!stack.peek().replace(this, membAccess)) {
					throw new Error("Couldn't replace a VariableAccess with a MemberAccess! Stack = "+stack.toString(true));
				}
				return Response.LOOP;
				//return Response.RESTART;
			}
		}
		
		/*
		TypeParam genType = getTypeParam(stack, name);
		if(genType != null) {
			ref = genType.getArgument();
			return Response.OK;
		}
	`	*/
		
		ref = getType(name, stack);
		if(ref != null) return Response.OK;
		
		if(fatal && ref == null) {
			String message = "Couldn't resolve access to "+name+".";
			String guess = guessCorrectName(stack, res);
			if(guess != null) {
				message += " Did you mean "+guess+" ?";
			}
			if(res.params.veryVerbose) {
				Thread.dumpStack();
				throw new OocCompilationError(this, stack, message+", btw, stack = "+stack.toString(true));
			}
			throw new OocCompilationError(this, stack, message);
		}
		
		return (ref == null) ? Response.LOOP : Response.OK;
		
	}

	private String guessCorrectName(NodeList<Node> mainStack, Resolver res) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		NodeList<VariableDecl> variables = new NodeList<VariableDecl>();
		
		Scope scope = (Scope) mainStack.get(mainStack.find(Scope.class));
		scope.getVariables(variables);
		
		for(VariableDecl decl: variables) {
			for(VariableDeclAtom atom: decl.getAtoms()) {
				int distance = Levenshtein.distance(name, atom.getName());
				if(distance < bestDistance) {
					bestDistance = distance;
					bestMatch = atom.getName();
				}
			}
		}
		
		if(bestDistance > 3) return null;
		return bestMatch;
		
	}

	@Override
	public String toString() {
		return name;
	}

	public String getUnderName() {
		if(ref instanceof TypeDecl) {
			return ((TypeDecl) ref).getUnderName();
		}
		return getName();
	}
	
	@Override
	public boolean canBeReferenced() {
		return true;
	}

}
