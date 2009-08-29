package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Instantiation extends FunctionCall {

	public Instantiation(FunctionCall call, Token startToken) {
		super(call.name, call.suffix, startToken);
		arguments.setAll(call.arguments);
	}

	public Instantiation(String name, String suffix, Token startToken) {
		super(name, suffix, startToken);
	}
	
	public Instantiation(Token startToken) {
		super("", "", startToken);
	}
	
	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException {
		
		if(name.isEmpty()) guessName(stack);
		
		TypeDecl decl = res.module.getType(name);
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
					+name+" for arguments "+getArgsRepr());
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
			name = ass.getLeft().getType().getName();
		} else if(stack.peek() instanceof VariableDeclAtom) {
			VariableDeclAtom vda = (VariableDeclAtom) stack.peek();
			if(vda.getExpression() == this) {
				VariableDecl vd = (VariableDecl) stack.get(stack.find(VariableDecl.class));
				if(vd.getType() == null) {
					throw new OocCompilationError(this, stack, "Couldn't guess type of 'new'"
							+stack.peek().getClass().getSimpleName()+")");
				}
				name = vd.getType().getName();
			}
		} else if(stack.peek() instanceof Cast) {
			name = ((Cast) stack.peek()).getType().getName();
		} else {
			throw new OocCompilationError(this, stack, "Couldn't guess type of 'new'");
		}
		
		return true;
		
	}
	
}
