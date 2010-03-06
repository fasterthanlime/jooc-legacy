package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Levenshtein;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class FunctionCall extends Access implements MustBeResolved {

	protected boolean dead = false;
	
	protected boolean superCall;
	protected String name;
	protected String suffix;
	protected final NodeList<Expression> typeParams;
	protected final NodeList<Expression> arguments;
	protected FunctionDecl impl;
	protected Expression returnArg;
	protected Type realType;
	
	public FunctionCall(String name, Token startToken) {
		this(name, null, startToken);
	}
	
	public FunctionCall(String name, String suffix, Token startToken) {
		super(startToken);
		this.name = name;
		this.suffix = suffix;
		this.typeParams = new NodeList<Expression>();
		this.arguments = new NodeList<Expression>(startToken);
		this.impl = null;
		this.returnArg = null;
		this.realType = null;
	}
	
	public FunctionCall(FunctionDecl func, Token startToken) {
		this(func.getName(), func.getSuffix(), startToken);
		setImpl(func);
	}
	
	public boolean isSuperCall() {
		return superCall;
	}
	
	public void setSuperCall(boolean superCall) {
		this.superCall = superCall;
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
	
	public NodeList<Expression> getTypeParams() {
		return typeParams;
	}
	
	public NodeList<Expression> getArguments() {
		return arguments;
	}
	
	public Expression getReturnArg() {
		return returnArg;
	}
	
	public void setReturnArg(Expression returnArg) {
		this.returnArg = returnArg;
	}

	public Type getType() {
		return realType;
	}
	
	private Type realTypize(Type typeArg, Resolver res, NodeList<Node> stack) {

		Type type = getRealType(typeArg, stack, res, true);
		if(type == null) {
			type = typeArg.clone();
		}
		
		int i = -1;
		for(Access exprParam: type.getTypeParams()) {
			i++;
			String name = "";
			if(exprParam instanceof VariableAccess) {
				name = ((VariableAccess) exprParam).getName();
			} else if(exprParam instanceof FunctionCall) {
				name = ((FunctionCall) exprParam).getName();
			}
			Access expr = getExprParam(name, stack, res, true);
			if(expr != null){
				type.getTypeParams().set(i, expr);
			}
		}
		
		return type;
		
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		typeParams.accept(visitor);
		arguments.accept(visitor);
		if(realType != null) realType.accept(visitor);
		if(returnArg != null) returnArg.accept(visitor);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == impl) {
			impl = (FunctionDecl) kiddo;
			return true;
		}
		return false;
	}

	public boolean isResolved() {
		return false;
	}

	public Response resolve(final NodeList<Node> stack, final Resolver res, final boolean fatal) {
		
		if(dead) return Response.OK;
		
		if(impl == null) {
			if (name.equals("this")) {
				resolveConstructorCall(stack, false);
			} else if (name.equals("super")) {
				resolveConstructorCall(stack, true);
			}  else {
				Response response = resolveRegular(stack, res, fatal);
				if(response != Response.OK) return response;
			}
		}
	
		if(impl != null) {
			autocast();
			Response response = handleGenerics(stack, res, fatal);
			if(response != Response.OK) return response;
		}
		
 		if(impl == null) {
 			if(fatal) {
 				String message = "No such function "+name+getArgsRepr()+".";
 				String guess = guessCorrectName(stack, res);
 				if(guess != null) {
 					message += " Did you mean "+guess+" ?";
 				}
 				throw new OocCompilationError(this, stack, message);
 			}
 			return Response.LOOP;
 		}
 		
 		return Response.OK;
		
	}

	protected Response handleGenerics(final NodeList<Node> stack, final Resolver res, boolean fatal) {

		if(dead) return Response.OK;
		
		if(impl == null) {
			if(fatal) throw new OocCompilationError(this, stack, "Didn't find implementation for "
					+this+", can't handle generics.");
			return Response.LOOP;
		}

		boolean andedSomearg = false;
		int argOffset = impl.hasThis() ? 1 : 0;
		for(int j = 0; j < impl.getArguments().size() - argOffset; j++) {
			Argument implArg = impl.getArguments().get(j + argOffset);
			
			if(implArg instanceof VarArg) { continue; }
            if(implArg.getType() == null || !implArg.getType().isResolved()) {
                // need ref arg type, we'll do it later
            	return Response.LOOP;
            }
            if(!(implArg.getType().isGeneric() &&
                 implArg.getType().getPointerLevel() == 0 &&
                 implArg.getType().getReferenceLevel() == 0)) { continue; }
			
			Expression callArg = arguments.get(j);
			if(callArg.getType() == null || !callArg.getType().isResolved()) {
				// need call arg type
				return Response.LOOP;
			}
			
            if(!(callArg instanceof AddressOf) && !callArg.getType().isGeneric()) {
            	arguments.set(j, new AddressOf(callArg, callArg.startToken));
                andedSomearg = true;
            }
		}
		
		// Find all variable accesses to fill this function's type params
		if(typeParams.size() < impl.getTypeParams().size()) {
			Iterator<TypeParam> iter = impl.getTypeParams().values().iterator();
			for(int i = 0; i < typeParams.size(); i++) iter.next();
			while(iter.hasNext()) {
				TypeParam typeParam = iter.next();
				Expression result = getExprParam(typeParam.getName(), stack, res, fatal);
				if(result == null) {
					if(fatal) throwUnresolvedType(stack, typeParam.getName(), res);
					return Response.LOOP;
				}
				typeParams.add(result);
			}
			//return Response.RESTART;
		}
		
		// Determine the real type of this function call.
		if(realType == null) {
			Type retType = impl.getReturnType();
			if(!retType.isResolved()) {
				// should know if it's generic or not
				return Response.LOOP;
			}
			if(retType.isGenericRecursive()) {
				Type candidate = realTypize(retType, res, stack);
				if(candidate == null) {
					if(fatal) throw new OocCompilationError(this, stack, "RealType still null, can't resolve generic type "+retType);
					return Response.LOOP;
				}
				realType = candidate;
			} else {
				realType = retType;
			}
		}
		
		/* Unwrap if needed */
		{
			Response response = unwrapIfNeeded(stack);
			if(response != Response.OK) return response;
		}

		/* Resolve returnArg */
		if(returnArg != null) {
 			if(returnArg instanceof MustBeResolved && !((MustBeResolved) returnArg).isResolved()) {
 				// need returnArg to be resolved
 				return Response.LOOP;
 			}
 			if(!(returnArg instanceof AddressOf)) {
 				returnArg = returnArg.getGenericOperand();
 			}
        }
		
		if(andedSomearg) return Response.LOOP;
		return Response.OK;
		
	}

	private Response unwrapIfNeeded(final NodeList<Node> stack) {
		Node parent = stack.peek();
        
        if(impl == null || impl.getReturnType() == null) {
            // need ref and refType
            return Response.LOOP;
        }
        
        int idx = 1;
        while(parent instanceof Cast) {
        	idx += 1;
            parent = stack.peek(idx);
        }
        
		if(impl.getReturnType().isGeneric() && !isFriendlyHost(parent)) {
			VariableDecl vDecl = new VariableDecl(getType(), false, startToken, stack.getModule());
            vDecl.getAtoms().add(new VariableDeclAtom(generateTempName("genCall", stack), null, startToken));
            stack.addBeforeLine(stack, vDecl);
            VariableAccess varAcc = new VariableAccess(vDecl, startToken);
            setReturnArg(varAcc);
            
            CommaSequence seq = new CommaSequence(startToken);
            seq.getBody().add(this);
            seq.getBody().add(varAcc);
            
            stack.peek().replace(this, seq);
            
            // just unwrapped
            return Response.LOOP;
        }
		
		return Response.OK;
	}
	
	/**
	 * In some cases, a generic function call needs to be unwrapped,
	 * e.g. when it's used as an expression in another call, etc.
	 * However, some nodes are 'friendly' parents to us, e.g.
	 * they handle things themselves and we don't need to unwrap.
	 * @return true if the node is friendly, false if it is not and we
	 * need to unwrap
	 */
    private boolean isFriendlyHost (Node node) {
    	return  (node instanceof Line) ||
				(node instanceof CommaSequence) ||
				(node instanceof VariableDeclAtom) ||
				(node instanceof Assignment);
    }

	protected final Type getRealType(Type typeArg, NodeList<Node> stack, Resolver res, boolean fatal) {

		Expression realExpr = getRealExpr(typeArg.getName(), stack, res, fatal);
		if(realExpr == null) {
			return null;
		}
		
		if(realExpr instanceof VariableAccess && ((VariableAccess) realExpr).getName().equals(typeArg.getName())) {
			return typeArg.clone();
		}
		
		return realExpr instanceof TypeParam ? null : realExpr.getType();
		
	}
	
	protected Expression getRealExpr(String typeParam, NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(impl == null) return null;
		
		Expression result = null;
		
		if(debugCondition()) System.out.println("[getRealExpr] Should getRealExpr of "+typeParam);
		
		int i = -1;
		boolean isFirst = true;
		for(Argument arg: impl.getArguments()) {
			if(isFirst && impl.hasThis()) {
				isFirst = false;
				continue;
			}
			i++;
			
			Expression callArg = arguments.get(i);
			
			// e.g. func <T> myFunc(T: Class), and arg = T
			if(arg.getName().equals(typeParam)) {
				result = callArg;
				if(res.params.veryVerbose || debugCondition()) 
					System.out.println("[getRealExpr] Matched <"+typeParam+"> with "+result+", argName-wise");
				break;
			}
			// e.g. func <T> myFunc(value: T), and arg = value.
			if(arg.getType().getName().equals(typeParam)) {
				// not resolved yet?
				if(callArg.getType() == null) return null;
				Type ourType = callArg.getType();
				// make it flat!
				if(!ourType.isFlat()) {
					ourType = ourType.clone();
					ourType.setPointerLevel(0);
					ourType.setReferenceLevel(0);
					ourType.setArray(false);
				}
				TypeAccess typeAcc = new TypeAccess(ourType, callArg.startToken);
				typeAcc.resolve(stack, res, fatal);
				result = typeAcc;
				if(res.params.veryVerbose || debugCondition())
					System.out.println("[getRealExpr] Matched <"+typeParam+"> with "+result+", varAccType-wise");
				break;
			}
			// e.g. func <T> myFunc(list: List<T>)
			if(arg.getType().isGenericRecursive()) {
				if(res.params.veryVerbose || debugCondition())
					System.out.println("[getRealExpr] "+arg.getType()+" is generic-recursive, trying to get <"+typeParam+"> in it.");
				result = searchTypeParam(typeParam, callArg.getType(), stack, res, fatal);
				if(result != null) {
					if(res.params.veryVerbose)
						System.out.println("[getRealExpr] Matched <"+typeParam+"> with "+result+", genericRecursive-wise");
					break;
				}
			}
		}
		
		return result;
		
	}
	
	/**
	 * Search for the type param @needle in the type @haystack
	 */
	private Expression searchTypeParam(String needle, Type haystack,
			NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(debugCondition()) System.out.println("[searchTypeParam] Looking for typeParam "+needle+" in type "+haystack);
		
		Declaration ref = haystack.getRef();
		if(ref instanceof TypeDecl) {
			TypeDecl typeDecl = (TypeDecl) ref;
			Iterator<String> keys = typeDecl.getTypeParams().keySet().iterator();
			int i = -1;
			while(keys.hasNext()) {
				i++;
				String key = keys.next();
				if(key.equals(needle)) {
					if(debugCondition()) System.out.println("[searchTypeParam] Found the needle "+needle+" in type "+haystack+", in typeDecl "+typeDecl);
					Type realType = getRealType(haystack, stack, res, fatal);
					if(realType != null && i < realType.getTypeParams().size()) {
						return realType.getTypeParams().get(i);
					}
				}
			}
		}
		
		return null;
	}

	protected Access getExprParam(String typeParam, NodeList<Node> stack, Resolver res, boolean fatal) {
	
		if(impl == null) return null;
		
		Access result = null;
		Expression callArg = getRealExpr(typeParam, stack, res, fatal);
		
		if(callArg != null && callArg.getType() != null) {
			if(callArg.getType().getName().equals("Class")) {
				result = (Access) callArg;
				if(debugCondition()) System.out.println("[getExprParam] callArg type name is 'Class'");
			} else if(callArg.getType().isGeneric()) {
				result = new VariableAccess(typeParam, callArg.startToken);
				if(debugCondition()) System.out.println("[getExprParam] callArg type is generic");
			} else {
				result = (Access) callArg;
				if(debugCondition()) System.out.println("[getExprParam] callArg-normal");
			}
		}
		
		if(debugCondition()) System.out.println("Found exprParam "+result+" for typeParam "+typeParam+" in "+this);
			
		return result;
	
	}

	// used to determine if debug messages should be printed (usually comparing a name)
	private boolean debugCondition() {
		return false;
	}

	protected void autocast() {
		if(impl == null) return;

		Iterator<Expression> callArgs = arguments.iterator();
		Iterator<Argument> implArgs = impl.getThisLessArgsIter();
		while(implArgs.hasNext() && callArgs.hasNext()) {
			Expression callArg = callArgs.next();
			Argument implArg = implArgs.next();
			if(implArg.getType() == null || callArg.getType() == null) {
				continue;
			}
			if(implArg.getType().isSuperOf(callArg.getType())
					&& implArg.getType().getRef() != null
					&& callArg.getType().getRef() != null) {
				arguments.replace(callArg, new Cast(callArg, implArg.getType(), callArg.startToken));
			}
		}
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
		for(Import imp: module.getGlobalImports()) {
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
		
		if(bestDistance > 3) return null;
		return bestMatch;
		
	}

	protected void resolveConstructorCall(final NodeList<Node> mainStack, final boolean isSuper) throws OocCompilationError {
		
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
			if(decl.getName().equals("init") && (suffix == null || decl.getSuffix().equals(suffix))) {
				if(matchesArgs(decl)) {
					impl = decl;
					return;
				}
			}
		}
		
	}
	
	protected Response resolveRegular(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		impl = getFunction(name, suffix, this, stack);

		if(impl == null) {
			Module module = (Module) stack.get(0);
			for(Import imp: module.getGlobalImports()) {
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
							// copy the module information for getFullName if it's global.
							if(varDecl.isGlobal())
								impl.module = varDecl.module;
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
						// copy the module information for getFullName if it's global.
						if(varDecl.isGlobal())
							impl.module = varDecl.module;
					} else {
						if(varDecl.getType() == null) return Response.OK;
						if(fatal) {
							throw new OocCompilationError(this, stack, "Trying to call "
								+name+", which isn't a function pointer (Func), but a "+varDecl.getType());
						}
						return Response.LOOP;
					}
				}
			}
		}

		if(impl != null) {
			if(impl.isMember()) {
				turnIntoMemberCall(stack, res);
				//return Response.RESTART;
				return Response.LOOP;
			}
		}
		
		return Response.OK;
		
	}

	private void turnIntoMemberCall(final NodeList<Node> stack, final Resolver res) {
		MemberCall memberCall = null;
		if(impl.isStatic()) {
			memberCall = new MemberCall(new VariableAccess(impl.getTypeDecl().getType().getName(), startToken), this, startToken);
		} else {
			VariableAccess thisAccess = new VariableAccess("this", startToken);
			thisAccess.resolve(stack, res, true);
			memberCall = new MemberCall(thisAccess, this, startToken);
		}
		memberCall.setImpl(impl);
		memberCall.setSuperCall(superCall);
		stack.peek().replace(this, memberCall);
		this.dead = true;
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
			Expression arg = iter.next();
			sB.append(arg.getType()+":"+arg);
			if(iter.hasNext()) sB.append(", ");
		}
		sB.append(')');
		
		return sB.toString();
	}

	public boolean isConstructorCall() {
		return name.equals("this") || name.equals("super");
	}
	
	public String getProtoRepr() {
		if(suffix == null || suffix.length() == 0) {
			return name+getArgsRepr();
		}
		return name+"~"+suffix+getArgsRepr();
	}
	
	@Override
	public String toString() {
		return getProtoRepr();
	}

	public int getScore(FunctionDecl decl) {
		int score = 0;
		
		NodeList<Argument> declArgs = decl.getArguments();
		if(matchesArgs(decl)) {
			score += 10;
		} else {
			return 0;
		}
		
		if(declArgs.size() == 0) return score;
		
		Iterator<Argument> declIter = declArgs.iterator();
		if(decl.hasThis() && declIter.hasNext()) declIter.next();
		Iterator<Expression> callIter = arguments.iterator();
		while(callIter.hasNext() && declIter.hasNext()) {
			Argument declArg = declIter.next();
			Expression callArg = callIter.next();
			if(declArg.getType() == null) {
				return -1;
			}
			if(declArg.getType().equals(callArg.getType())) {
				score += 10;
			}
			if(declArg.getType().isSuperOf(callArg.getType())) {
				score += 5;
			}
		}
		
		return score;
	}

	public void throwUnresolvedType(NodeList<Node> stack, String typeName, Resolver res) {
	
		if(res.params.veryVerbose) {
			Thread.dumpStack();
		}
		
		if(impl != null) {
			throw new OocCompilationError(this, stack, "Couldn't figure out generic type <"+typeName+"> for call to "+impl);
		}
		throw new OocCompilationError(this, stack, "Couldn't figure out generic type <"+typeName+"> for call to "+getProtoRepr());
		
	}

	public String getFullName() {
		if(impl.module != null)
			return impl.module.getMemberPrefix() + getName();
		return getName();
	}
}
