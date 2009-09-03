package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class FunctionCall extends Access implements MustBeResolved {

	protected String name;
	protected String suffix;
	protected final NodeList<Expression> arguments;
	protected FunctionDecl impl;
	protected AddressOf returnArg;
	
	public FunctionCall(String name, String suffix, Token startToken) {
		super(startToken);
		this.name = name;
		this.suffix = suffix;
		this.arguments = new NodeList<Expression>(startToken);
		this.impl = null;
		this.returnArg = null;
	}
	
	public FunctionCall(FunctionDecl func, Token startToken) {
		this(func.getName(), func.getSuffix(), startToken);
		setImpl(func);
	}

	public void setImpl(FunctionDecl impl) {
		this.impl = impl;
	}
	
	public FunctionDecl getImpl() {
		return impl;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public NodeList<Expression> getArguments() {
		return arguments;
	}
	
	public AddressOf getReturnArg() {
		return returnArg;
	}

	@Override
	public Type getType() {
		if(impl != null) {
			return impl.getReturnType();
		}
		return null;
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
		arguments.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == impl) {
			impl = (FunctionDecl) kiddo;
			return true;
		}
		return false;
	}

	@Override
	public boolean isResolved() {
		return impl != null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean resolve(final NodeList<Node> stack, final Resolver res, final boolean fatal) throws IOException {

		if(impl == null) {
			if (name.equals("this")) resolveConstructorCall(stack, false);
			else if (name.equals("super")) resolveConstructorCall(stack, true);
			else resolveRegular(stack, res, fatal);
		}
	
		if(impl != null) {
			LinkedHashMap<String, GenericType> params = impl.getGenericTypes();
			if(!params.isEmpty()) {
				for(GenericType param: params.values()) {
					NodeList<Argument> implArgs = impl.getArguments();
					for(int i = 0; i < implArgs.size(); i++) {
						Argument arg = implArgs.get(i);
						if(!arg.getType().getName().equals(param.getName())) continue;
						Expression expr = arguments.get(i);
						if(!(expr instanceof VariableAccess)) {
							VariableDeclFromExpr vdfe = new VariableDeclFromExpr(
									generateTempName(param.getName()+"param"), expr, startToken);
							arguments.replace(expr, vdfe);
							stack.push(arguments);
							vdfe.unwrapToVarAcc(stack);
							stack.pop(arguments);
						}
					}
				}
			}
		}
		
		if(impl != null) {
			Type returnType = impl.getReturnType();
			GenericType genType = impl.getGenericTypes().get(returnType.getName());
			if(genType != null) {
				Node parent = stack.peek();
				if(parent instanceof Assignment) {
					Assignment ass = (Assignment) parent;
					if(ass.getLeft() instanceof Access) {
						returnArg = new AddressOf(ass.getLeft(), startToken);
						stack.get(stack.size() - 2).replace(ass, this);
					}
				} else if(parent instanceof VariableDeclAtom) {
					VariableDeclAtom atom = (VariableDeclAtom) parent;
					int varDeclIndex = stack.find(VariableDecl.class);
					VariableDecl decl = (VariableDecl) stack.get(varDeclIndex);
					parent.replace(this, null);
					
					int lineIndex = stack.find(Line.class, varDeclIndex);
					Line line = (Line) stack.get(lineIndex);
					
					if(decl instanceof VariableDeclFromExpr) {
						VariableDecl newDecl = new VariableDecl(getRealType(genType), false, startToken);
						newDecl.getAtoms().add(atom);
						stack.get(varDeclIndex - 1).replace(decl, newDecl);
						decl = newDecl;
					}
					
					NodeList<Line> list = (NodeList<Line>) stack.get(lineIndex - 1);
					VariableAccess varAcc = new VariableAccess(atom.getName(), startToken);
					varAcc.setRef(decl);
					returnArg = new AddressOf(varAcc, startToken);
					list.addAfter(line, new Line(this));
				}
			}
		}
		
		if(impl != null) {
			autocast();
		}
		
		if(impl == null && fatal) {
			String message = "Couldn't resolve call to function "+name+getArgsRepr()+".";
			String guess = guessCorrectName(stack, res);
			if(guess != null) {
				message += " Did you mean "+guess+" ?";
			}
			throw new OocCompilationError(this, stack, message);
		}
		
		return impl == null;
		
	}

	private void autocast() {
		if(impl == null) return;

		Iterator<Expression> callArgs = arguments.iterator();
		Iterator<Argument> implArgs = impl.getArguments().iterator();
		if(impl.hasThis() && implArgs.hasNext()) implArgs.next();
		
		while(implArgs.hasNext() && callArgs.hasNext()) {
			Expression callArg = callArgs.next();
			Argument implArg = implArgs.next();
			if(implArg.getType().isSuperOf(callArg.getType())) {
				System.out.println("Autocasting "+callArg+"");
				arguments.replace(callArg, new Cast(callArg, implArg.getType(), callArg.startToken));
			}
		}
	}

	private Type getRealType(GenericType genType) {
		int i = 0;
		for(Argument arg: impl.getArguments()) {
			if(arg.getType().getName().equals(genType.getName())) {
				return arguments.get(i).getType();
			}
			i++;
		}
		return null;
	}

	protected String guessCorrectName(final NodeList<Node> mainStack, final Resolver res) {
		
		int bestDistance = Integer.MAX_VALUE;
		String bestMatch = null;
		
		NodeList<FunctionDecl> funcs = new NodeList<FunctionDecl>();
		
		for(int i = mainStack.size() - 1; i >= 0; i--) {
			Node node = mainStack.get(i);
			if(!(node instanceof Scope)) continue;
			((Scope) node).getFunctions(funcs);
		}
		
		for(FunctionDecl decl: funcs) {
			int distance = Levenshtein.distance(name, decl.getName());
			if(distance < bestDistance) {
				bestDistance = distance;
				bestMatch = decl.getProtoRepr();
			}
		}
		
		Module module = (Module) mainStack.get(0);
		for(Import imp: module.getImports()) {
			for(Node node: imp.getModule().body) {
				if(node instanceof FunctionDecl) {
					FunctionDecl decl = (FunctionDecl) node;
					int distance = Levenshtein.distance(name, decl.getName());
					if(distance < bestDistance) {
						bestDistance = distance;
						bestMatch = decl.getProtoRepr();
					}
				}
			}
		}
		
		return bestMatch;
		
	}

	protected void resolveConstructorCall(final NodeList<Node> mainStack, final boolean isSuper) throws OocCompilationError, EOFException {
		
		int typeIndex = mainStack.find(TypeDecl.class);
		if(typeIndex == -1) {
			throw new OocCompilationError(this, mainStack, (isSuper ? "super" : "this")
					+getArgsRepr()+" call outside a class declaration, doesn't make sense.");
		}
		TypeDecl typeDecl = (TypeDecl) mainStack.get(typeIndex);
		if(isSuper) {
			if(!(typeDecl instanceof ClassDecl)) {
				throw new OocCompilationError(this, mainStack, "super"+getArgsRepr()+" call in type def "
						+typeDecl.getName()+" which is not a class! wtf?");
			}
			ClassDecl classDecl = ((ClassDecl) typeDecl);
			if(classDecl.getSuperRef() == null) {
				throw new OocCompilationError(this, mainStack, "super"+getArgsRepr()+" call in class "
						+typeDecl.getName()+" which has no super-class!");
			}
			typeDecl = classDecl.getSuperRef();
		}
		
		for(FunctionDecl decl: typeDecl.getFunctions()) {
			if(decl.getName().equals("init")) {
				if(matchesArgs(decl)) {
					impl = decl;
					return;
				}
			}
		}
		
	}
	
	protected void resolveRegular(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException {
		
		impl = getFunction(name, suffix, this, stack);

		if(impl == null) {
			Module module = (Module) stack.get(0);
			for(Import imp: module.getImports()) {
				searchIn(imp.getModule());
				if(impl != null) break;
			}
		}
		
		if(impl == null) {
			int typeIndex = stack.find(TypeDecl.class);
			if(typeIndex != -1) {
				TypeDecl typeDeclaration = (TypeDecl) stack.get(typeIndex);
				for(VariableDecl varDecl: typeDeclaration.getVariables()) {
					if(varDecl.getType() instanceof FuncType && varDecl.getName().equals(name)) {
						FuncType funcType = (FuncType) varDecl.getType();
						if(matchesArgs(funcType.getDecl())) {
							impl = funcType.getDecl();
							break;
						}
					}
				}
			}
		}
		
		if(impl == null) {
			VariableDecl varDecl = getVariable(name, stack);
			if(varDecl != null) {
				if(varDecl.getName().equals(name)) {
					if(varDecl.getType() instanceof FuncType) {
						FuncType funcType = (FuncType) varDecl.getType();
						impl = funcType.getDecl();
					} else {
						throw new OocCompilationError(this, stack, "Trying to call "
								+name+", which isn't a function pointer (Func), but a "+varDecl.getType());
					}
				}
			}
		}

		if(impl != null) {
			if(impl.isMember()) transformToMemberCall(stack, res);
		}
		
	}

	private void transformToMemberCall(final NodeList<Node> stack,
			final Resolver res) throws IOException {
		VariableAccess thisAccess = new VariableAccess("this", startToken);
		thisAccess.resolve(stack, res, true);
		MemberCall memberCall = new MemberCall(new VariableAccess("this", startToken), this, startToken);
		memberCall.setImpl(impl);
		stack.peek().replace(this, memberCall);
	}
	
	protected void searchIn(Module module) {
		for(Node node: module.getBody()) {
			if(node instanceof FunctionDecl) {
				FunctionDecl decl = (FunctionDecl) node;
				if(matches(decl)) {
					impl = decl;
					return;
				}
			}
		}
	}

	public boolean matches(FunctionDecl decl) {
		return matchesName(decl) && matchesArgs(decl);
	}

	public boolean matchesArgs(FunctionDecl decl) {
		int numArgs = decl.getArguments().size();
		if(decl.hasThis()) numArgs--;
		
		if(numArgs == arguments.size()
			|| ((numArgs > 0 && decl.getArguments().getLast() instanceof VarArg)
			&& (numArgs - 1 <= arguments.size()))) {
			return true;
		}
		return false;
	}

	public boolean matchesName(FunctionDecl decl) {
		return decl.isNamed(name, suffix);
	}
	
	public String getArgsRepr() {
		StringBuilder sB = new StringBuilder();
		sB.append('(');
		Iterator<Expression> iter = arguments.iterator();
		while(iter.hasNext()) {
			sB.append(iter.next().getType());
			if(iter.hasNext()) sB.append(", ");
		}
		sB.append(')');
		
		return sB.toString();
	}

	public boolean isConstructorCall() {
		return name.equals("this") || name.equals("super");
	}
	
	public String getProtoRepr() {
		return name+getArgsRepr();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+": "+getProtoRepr();
	}

	public int getScore(FunctionDecl func) {
		int score = 0;
		
		NodeList<Argument> declArgs = func.getArguments();
		if(matchesArgs(func)) {
			score += 10;
		} else {
			return 0;
		}
		
		if(declArgs.size == 0) return score;
		
		Iterator<Argument> declIter = declArgs.iterator();
		if(func.hasThis() && declIter.hasNext()) declIter.next();
		Iterator<Expression> callIter = arguments.iterator();
		while(callIter.hasNext()) {
			Argument declArg = declIter.next();
			Expression callArg = callIter.next();
			if(declArg.getType().equals(callArg.getType())) {
				score += 10;
			}
		}
		
		return score;
	}
	
}
