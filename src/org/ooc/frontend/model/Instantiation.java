package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Instantiation extends FunctionCall {

	protected Type type;

	public Instantiation(Type type, String suffix, Token startToken) {
		super("", suffix, startToken);
		this.type = type;
	}
	
	public Instantiation(Token startToken) {
		super("", "", startToken);
		this.type = null;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException {
		
		if(type == null) guessName(stack);
		
		TypeDecl decl = res.module.getType(type.getName());
		if(decl != null) {
			for(FunctionDecl func: decl.getFunctions()) {
				if(!func.isConstructor()) continue;
				if(!suffix.isEmpty() && !func.getSuffix().equals(suffix)) continue;
				int numArgs = func.getArguments().size();
				if(decl instanceof ClassDecl) numArgs--; // ignore the 'this'
				if(numArgs == arguments.size()
					|| ((!func.getArguments().isEmpty() && func.getArguments().getLast() instanceof VarArg)
					&& (numArgs <= arguments.size()))) {
					impl = func;
					return false;
				}
			}
		}
		
		if(fatal && impl == null) {
			throw new OocCompilationError(this, stack, "Couldn't find a constructor in "
					+type+" for arguments "+getArgsRepr());
		}
		
		return impl == null;
		
	}

	/**
	 * Guess the type of nameless 'new' calls, e.g.
	 * <code>
	 * class Blah {}
	 * Blah b = new; // guessed: new Blah()
	 * 
	 * func accept(Blah b) {
	 *   // [...]
	 * }
	 * 
	 * accept(new); // guessed: new Blah()
	 * </code>
	 * @throws EOFException 
	 */
	protected boolean guessName(NodeList<Node> stack) throws Error, EOFException {
		
		if(stack.peek() instanceof Assignment) {
			Assignment ass = (Assignment) stack.peek();
			if (ass.getLeft().getType() == null) return false;
			type = ass.getLeft().getType();
		} else if(stack.peek() instanceof VariableDeclAtom) {
			VariableDeclAtom vda = (VariableDeclAtom) stack.peek();
			if(vda.getExpression() == this) {
				VariableDecl vd = (VariableDecl) stack.get(stack.find(VariableDecl.class));
				if(vd.getType() == null) {
					throw new OocCompilationError(this, stack, "Couldn't guess type of 'new'"
							+stack.peek().getClass().getSimpleName()+")");
				}
				type = vd.getType();
			}
		} else if(stack.peek() instanceof Cast) {
			type = ((Cast) stack.peek()).getType();
		} else {
			throw new OocCompilationError(this, stack, "Couldn't guess type of 'new'");
		}
		
		return true;
		
	}
	
}
