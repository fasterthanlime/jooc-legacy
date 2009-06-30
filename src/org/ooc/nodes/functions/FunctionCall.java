package org.ooc.nodes.functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.nodes.array.Subscript;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.Instantiation;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.numeric.IntLiteral;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.others.Comma;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.Parenthesis;
import org.ooc.nodes.others.TransparentBlock;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Clazz;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A call to a function, e.g. "myFunc(arg1, arg2)"
 *
 * @author Amos Wenger
 */
public class FunctionCall extends Parenthesis {

	/**
	 * The name of the function being called, e.g. for "myFunc(arg1, arg2)", name = "myFunc"
	 */
    public String name;
    
    /**
     * The best found implementation of the function we're trying to call. 
     */
    public Function impl;
    
    /**
     * The class this function belongs to, or null.
     */
    public Clazz clazz;

    private boolean memberFunctionCheck = false;

    /**
     * Default constructor. Create a function call with specified name.
     * @param location
     * @param name
     */
    public FunctionCall(FileLocation location, String name) {
        super(location);
        this.name = name;
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	writeToCSource(a, true);
    }
    	
    protected void writeToCSource(Appendable a, boolean writeSpacing) throws IOException {
    	
    	if(writeSpacing) {
    		writeWhitespace(a);
    	}
    	
        if(impl == null) {
        	a.append(name);
        } else {
        	if(this instanceof Instantiation || impl.isStatic) {
				String name = impl.getMangledName(clazz);
				a.append(name);
            } else {
                a.append(impl.getMangledName(null));
            }
        }
        super.writeToCSource(a);
        
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {

        if(!assembleAll(manager)) {
            return;
        }

        if(getParent() == null) {
        	return;
        }
        
        SyntaxNode prev = getParent().getPrev(this);
        if(prev == null || !(prev instanceof Dot)) {
	        ClassDef classDef = getParent().getNearest(ClassDef.class);
	        if(classDef != null) {
	        	if(name.equals("this")) {
	        		
	        		handleThisCall(manager, classDef);
        			return;
	        		
	        	} else if(name.equals("super")) {
	        		
	        		handleSuperCall(manager, classDef);
        			return;
	        		
	        	} else if(classDef.clazz.hasUnmangledFunction(name)) {
	        		
					MemberFunctionCall memberFuncCall = new MemberFunctionCall(location, name, this.nodes, new VariableAccess(location, classDef.clazz.getThis()));
					replaceWith(manager, memberFuncCall);
	                return;
	                
	            } else if(!memberFunctionCheck) {
	            	
	                memberFunctionCheck = true;
	                manager.queue(this, "Hoping that "+name+"(...) is a member function");
	                return;
	                
	            }
	        }
        }

        if(this.name.equals("free")) {
            manager.warn("Call to free() found "+location+". OOC uses a Garbage Collector by default " +
            		"(it replaces the default malloc). If you free() something which has been " +
            		"malloc()ed in OOC, your program will crash!", this);
        } else if(this.name.equals("malloc")) {
        	this.name = "GC_malloc";
        } else if(this.name.equals("realloc")) {
        	this.name = "GC_realloc";
        } else if(this.name.equals("calloc")) {
        	this.name = "GC_calloc";
        }

        if(prev != null && prev instanceof Dot) {
            SyntaxNode prevPrev = getParent().getPrev(prev);
            if(prevPrev instanceof Subscript || prevPrev instanceof Name || prevPrev.getClass() == Parenthesis.class && ((Parenthesis) prevPrev).nodes.size() <= 2) {
            	
            	String reason = "Hoping for "+prevPrev.getClass().getSimpleName()+" '"+prevPrev+"' "
            	+" to assemble more.";
                manager.queue(prevPrev, reason);
				manager.queue(this, reason);
                return;
                
            } else if(prevPrev instanceof VariableAccess) {
            	
                VariableAccess access = (VariableAccess) prevPrev;
                if(manager.isDirty(prevPrev)) {
                	manager.queue(prevPrev, "Dirty prevPrev-VariableAccess of FunctionCall, hoping for it to finish assembling");
                	manager.queue(this, "Dirty prevPrev-VariableAccess of FunctionCall, hoping for it to finish assembling");
                	return;
                }
                getParent().remove(prev);
                getParent().remove(prevPrev);
                MemberFunctionCall memberFuncCall = new MemberFunctionCall(location, name, this.nodes, access);
				replaceWith(manager, memberFuncCall);
                return;
                
            } else if(prevPrev instanceof FunctionCall) {
            	
            	FunctionCall call = (FunctionCall) prevPrev;
            	if(manager.isDirty(prevPrev)) {
            		String reason = "prevPrev (which is a FunctionCall) to "+call.name+"() is dirty, returning.";
					manager.queue(prevPrev, reason);
            		manager.queue(this, reason);
            	} else {
            		handleCascadeCall(manager, call);
            	}
            	return;
            	
            }
        }
        
        if(impl == null) {
        	List<FunctionDef> defs = manager.getContext().source.getRoot().getNodesTyped(FunctionDef.class, false);
        	List<Function> candidates = new ArrayList<Function>();
        	for(FunctionDef def: defs) {
        		if(def.function.getSimpleName().equals(name)) {
        			candidates.add(def.function);
        		}
        	}
        	TypedArgumentList tal = new TypedArgumentList(this);
        	impl = FunctionDef.getImplementation(manager.getContext(), candidates, tal);
        }
        
    }

	private boolean handleSuperCall(AssemblyManager manager,
			ClassDef classDef) throws CompilationFailedError {
		
		if(classDef == null) {
			manager.queue(this, "Trying to handle a super() call but can't resolve ClassDef :/");
			return false;
		}
		
		FunctionDef funcDef = getNearest(FunctionDef.class);
		if(funcDef == null || !funcDef.function.isConstructor()) {
			manager.errAndFail("Call to a super-constructor is valid only in constructors", this);
			return false;
		}
		
		if(getNearestPrevNotTyped(TransparentBlock.class) != null) {
			manager.warn("Call #"+hash+" to a super-constructor should be the first statement in a constructor. (There's a "+getPrev().getDescription()+" before)", this);
			//return false;
		}
		
		if(classDef.clazz.zuperFullName.isEmpty()) {
			manager.errAndFail("Call to a super-constructor in a class that has no super-class.", this);
			return false;
		}
		
		Clazz zuper = classDef.clazz.getZuper(manager.getContext());
		if(zuper == null) {
			manager.queue(this, "Class "+classDef.clazz.zuperFullName+", super-class of "+classDef.clazz.fullName+" should assemble first.");
			return false;
		}
		
		ClassDef zuperDef = manager.getContext().getClassDef(zuper.fullName);
		if(zuperDef == null) {
			manager.queue(this, "Class "+classDef.clazz.zuperFullName+", super-class of "+classDef.clazz.fullName+" should assemble first.");
			return false;
		}
		
		Function superConstructor = zuperDef.getImplementation(manager.getContext(), "new", new TypedArgumentList(this));					
		if(superConstructor == null) {
			manager.errAndFail("No matching super-constructor found for the super"+getDescription(nodes)+" call", this);
			return false; // eclipse, stop freaking out.
		}
		
		FunctionDef superConstructorDef = zuperDef.getFunctionDef(superConstructor);
		if(manager.isDirtyRecursive(superConstructorDef)) {
			manager.queue(this, "Waiting on super constructor to assemble for super"+getDescription(nodes)+" call =)");
			return false;
		}
		
		templateFromFunction(superConstructorDef, manager);
		return true;
		
	}
	
	private boolean handleThisCall(AssemblyManager manager,
			ClassDef classDef) throws CompilationFailedError {
		
		if(classDef == null) {
			manager.queue(this, "Trying to handle a this() call but can't resolve ClassDef :/");
			return true;
		}
		
		FunctionDef funcDef = getNearest(FunctionDef.class);
		if(funcDef == null || !funcDef.function.isConstructor()) {
			manager.errAndFail("Call to another constructor is valid only in constructors. Hierarchy = "+getHierarchyRepr(), this);
			return true;
		}
		
		if(getNearestPrevNotTyped(TransparentBlock.class) != null) {
			manager.errAndFail("Call #"+hash+" to another constructor should be the first statement in a constructor. (There's a "+getPrev().getDescription()+" before)", this);
			return true;
		}
		
		TypedArgumentList tal = new TypedArgumentList(this);
		Function otherConstructor = classDef.getImplementation(manager.getContext(), "new", tal);					
		if(otherConstructor == null) {
			manager.errAndFail("No matching constructor found for the this"+tal+" call", this);
			return true; // eclipse, stop freaking out.
		}
		
		if(otherConstructor == getNearest(FunctionDef.class).function) {
			manager.errAndFail("Trying to call same constructor with this"+tal+" call.", this);
			return true; // eclipse, stop freaking out.
		}
		
		FunctionDef otherConstructorDef = classDef.getFunctionDef(otherConstructor);
		if(manager.isDirtyRecursive(otherConstructorDef)) {
			manager.queue(this, "Waiting on other constructor to assemble for this"+getDescription(nodes)+" call =)");
			return false;
		}
		
		templateFromFunction(otherConstructorDef, manager);
		return true;
		
	}

	private void templateFromFunction(FunctionDef source, AssemblyManager manager) {
		
		SyntaxNodeList code = new SyntaxNodeList(location);
		
		ListIterator<SyntaxNode> it = nodes.listIterator();
		for(Variable arg: source.function.args.list) {
			code.add(new VariableDecl(location, arg));
			code.add(new Assignment(location));
			while(it.hasNext()) {
				SyntaxNode node = it.next();
				if(node instanceof Comma) {
					break;
				}
				code.add(node);
			}
			code.add(new LineSeparator(location));
		}
		code.addAll(source);
		replaceWith(manager, code);
		
	}

	private void handleCascadeCall(AssemblyManager manager, FunctionCall call) {
		
		getPrev().drop(); // Remove the Dot
		
		SyntaxNode beforeNode = call;
		search: while(true) {
			if(beforeNode.getParent() instanceof Parenthesis) {
				beforeNode = beforeNode.getParent();
			} else if(beforeNode.getParent().getPrev(beforeNode) == null) {
				break search;
			} else if(beforeNode instanceof LineSeparator || beforeNode instanceof Scope) {
				beforeNode = beforeNode.getParent().getNext(beforeNode);
				break search;
			} else {
				beforeNode = beforeNode.getParent().getPrev(beforeNode);
			}
		}
		
		Scope parentScope = beforeNode.getParent().getNearest(Scope.class);
		Variable tempVariable = parentScope.generateTempVariable(call.getType(), call.name.replace("->", SEPARATOR));
		
		SyntaxNodeList tempVariableCode = new SyntaxNodeList(location);
		beforeNode.getParent().addBefore(beforeNode, tempVariableCode);
		
		tempVariableCode.add(new VariableDecl(location, tempVariable));
		tempVariableCode.add(new Assignment(location));
		call.moveTo(tempVariableCode);
		tempVariableCode.add(new LineSeparator(location));
		tempVariableCode.flatten();
		
		getParent().addBefore(this, new VariableAccess(location, tempVariable));
		getParent().addBefore(this, new Dot(location));
		
		manager.queueRecursive(getParent(), "Trying to recover from a cascade call, queuing recursively from the parent down."); // I think it's simpler.
		
	}
    
    
    @Override
	public String getDescription() {
    	return "function call to "+name+getDescription(nodes)+location;
    }
    
    protected String getDescription(List<SyntaxNode> nodes) {
    	StringBuilder builder = new StringBuilder();
    	builder.append('(');
    	for(SyntaxNode node: nodes) {
    		builder.append(node.toString());
    	}
    	builder.append(')');
    	return builder.toString();
    }

	
	@Override
	public Type getType() {

		if(impl == null) {
			Type type;
			// FIXME C headers should be parsed / interface files should be written
			// for C functions instead of having these horrible special cases here
			if(name.equals("malloc") || name.equals("calloc") || name.equals("realloc")) {
				type = Type.OBJECT; 
			} else if(name.equals("sizeof")) {
				type = IntLiteral.type;
			} else {
				type = Type.UNRESOLVED;
			}
			return type;
		}
		
		return impl.returnType;
	}
	
	
	@Override
	protected boolean isSpaced() {
		return true;
	}
	
	/**
	 * @return a human-friendly string representation of this function's arguments,
	 * including opening and closing parenthesis.
	 * Can be used in development tools, for visualization.
	 */
	public String getArgsRepr() {
		
		Type.resolveCheckEnabled = false;
		StringBuilder builder = new StringBuilder("(");
		try {
			for(SyntaxNode node: nodes) {
				node.writeToCSource(builder);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		builder.append(")");
		Type.resolveCheckEnabled = true;
		return builder.toString();
		
	}
	
	/**
	 * @return a human-friendly string representation of this function's arguments' types,
	 * including opening and closing parenthesis.
	 * Can be used in development tools, for visualization.
	 */
	public String getArgTypesRepr() {
		
		Type.resolveCheckEnabled = false;
		
		StringBuilder builder = new StringBuilder("(");
		try {
			for(SyntaxNode node: nodes) {
				if(node instanceof Typed) {
					Typed typed = ((Typed) node);
					builder.append(typed.getType().getDescription());
				} else {
					node.writeToCSource(builder);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		builder.append(")");
		
		Type.resolveCheckEnabled = true;
		
		return builder.toString();
		
	}

	
}
