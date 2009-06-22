package org.ooc.nodes.functions;

import java.io.IOException;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.control.Return;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.doc.OocDocComment;
import org.ooc.nodes.interfaces.Commentable;
import org.ooc.nodes.interfaces.PotentiallyAbstract;
import org.ooc.nodes.interfaces.PotentiallyStatic;
import org.ooc.nodes.interfaces.PotentiallyUnmangled;
import org.ooc.nodes.numeric.IntLiteral;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Clazz;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A function definition, e.g. its prototype/signature and its body, if it's
 * a concrete function.
 * 
 * @author Amos Wenger
 */
public class FunctionDef extends Scope
	implements PotentiallyAbstract, PotentiallyUnmangled, PotentiallyStatic, Commentable {
	
	/**
	 * This structure contains most of the needed information about this function.
	 */
    public Function function;
    
    /**
     * The name of the variable which refers to the current instance of the class
     * in which we're defining the function.
     * It can be "this2" in some circumstances, e.g. with the callback hack.
     */
    public String thisFieldName = "this";

	protected OocDocComment comment;

    /**
     * Default constructor.
     * @param location
     * @param function
     */
    public FunctionDef(FileLocation location, Function function) {
        super(location);
        this.function = function;
        this.function.args.setContext(this);
    }
    
    @Override
    public void writeToCHeader(Appendable a) throws IOException {
    	
    	// Member functions are written in ClassDef, others are written here.
    	if(function.clazz == null) {
    		function.writePrototype(a, null);
            a.append(";\n");
    	}
    	
    }

	@Override
    public void writeToCSource(Appendable a) throws IOException {
		
        if(function.isAbstract) {
            return;
        }

        if(comment != null) {
			comment.writeToCSource(a);
		} else {
			writeWhitespace(a, -1);
		}
        writePrototype(a, function.clazz != null && !function.isStatic);
        a.append(" {\n");
        	
        if(function.isNamed("main") && function.clazz == null) {
        	a.append("\n");
        	writeIndent(a);
            a.append("GC_init();");
        }
        
        writeBody(a);
        writeWhitespace(a, -1);
        a.append("\n}");

    }

	protected void writeBody(Appendable a) throws IOException {
		
		writeIndent(a);
        for(SyntaxNode node: nodes) {
            node.writeToCSource(a);
        }
        
	}

	protected void writePrototype(Appendable a, boolean includeThis) throws IOException {
		
		function.returnType.writeToCSource(a);
        a.append(' ');
        a.append(function.getMangledName());
        a.append('(');

        if(includeThis) {
            function.clazz.getType().writeToCSource(a);
            a.append(" ");
			a.append(thisFieldName);
            if(!function.args.list.isEmpty()) {
                a.append(", ");
            }
        }
        
        int count = 0;
        for(Variable arg: function.args.list) {
            a.append(arg.toString());
            if(++count < function.args.list.size()) {
                a.append(", ");
            }
        }
        a.append(')');

	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		assembleAll(manager);
		function.args.assembleForce(manager);
		function.returnType.assembleForce(manager);
		
		if(!function.returnType.isResolved || manager.isDirty(function.args)) {
        	manager.queue(this, "Type '"+function.returnType.name+"' can't be resolved.");
        	return;
        }
		
		if(function.isNamed("new") && function.clazz != null && !(this instanceof ConstructorDef)) {
			ConstructorDef candidate = new ConstructorDef(location, function);
			candidate.addAll(this);
			candidate.comment = comment;
			replaceWith(manager, candidate);
			return;
		} else if(function.isNamed("main") && function.clazz == null && !IntLiteral.type.equals(function.returnType)) {
			if(!Type.VOID.equals(function.returnType)) {
				manager.warn("Funny return type '"+function.returnType.getDescription()+"' for main. Should be either 'Void' or 'Int'. Replaced with 'Int' anyway.", this);
			}
			function.returnType = IntLiteral.type;
		}
		
		ClassDef def = null;
		Variable returnMember = null;
		if(function.clazz != null) {
			def = function.clazz.getClassDef();
			if(def != null) {			
				for(Variable arg: function.args.list) {
					if(arg.type.equals(Type.UNRESOLVED)) {
						Variable member = def.getMember(manager.getContext(), arg.getName());
						if(member != null) {
							arg.type = member.type; // We resolved its type! Nice =)
						}
					}
				}
				
				if(function.args.list.isEmpty()) {
					returnMember = def.getMember(manager.getContext(), function.getSimpleName());
					if(returnMember != null) {
						if(function.returnType.equals(Type.VOID)) {
							function.returnType = returnMember.type; // We resolved its type! Nice =)
						}
					}
				}
			}
		}
		
		if(!function.isAbstract && !function.returnType.equals(Type.VOID) && getNodesTyped(Return.class, true).isEmpty()
				&& !(this instanceof ConstructorDef) && !function.isNamed("main")) {
			
			FileLocation location = nodes.isEmpty() ? this.location : nodes.get(nodes.size() - 1).location;
			
			if(returnMember != null && def != null) {
				
				add(new Return(location));
				add(new MemberAccess(location, def.clazz.getThis(), returnMember));
				add(new LineSeparator(location));
				
			} else if(!nodes.isEmpty()) {
				
				SyntaxNode last = nodes.get(nodes.size() - 1);
				LineSeparator sep = last.getNearestPrevTyped(LineSeparator.class);
				addAfter(sep, new Return(location));
				
			} else {
			
				throw new CompilationFailedError(location, "Function "+function.getSimplePrototype()
						+" should return a "+function.returnType.getDescription());
				
			}
			
		}
		
	}

	/**
	 * @param destClazz
	 * @return a copy of this function as if it was a member function of class
	 * 'destClazz'
	 */
    public FunctionDef copyInClass(Clazz destClazz) {
    	
        Function newFunc = this.function.copyInClass(destClazz);
        FunctionDef funcDef = new FunctionDef(location, newFunc);
        funcDef.addAll(this);
        return funcDef;
        
    }

    @Override
    public Variable getVariable(String name) {
    	
        Variable variable = super.getVariable(name);
        if(variable == null) {
    		for(Variable arg: function.args.list) {
    		    if(arg.getName().equals(name)) {
    		        variable = arg;
    		        break;
    		    }
    		}
        }
        return variable;
        
    }

    @Override
    protected boolean isIndented() {
    	
        return true;
        
    }

    @Override
    public void setAbstract(boolean isAbstract) {
    	
        function.isAbstract = isAbstract;
        
    }
    
    @Override
	public boolean isAbstract() {

		return function.isAbstract;
		
	}
    
    @Override
    public void setUnmangled(boolean isUnmangled) {
    
    	function.isUnmangled = isUnmangled;
    	
    }
    
    @Override
	public boolean isUnmangled() {

		return function.isUnmangled;
		
	}
    
    @Override
	public void setStatic(boolean isStatic) {
		
    	function.setStatic(isStatic);
		
	}
    
	@Override
	public boolean isStatic() {

		return function.isStatic();
		
	}


	@Override
	public String getDescription() {
		
		return (isStatic() ? "static " : "")+"function "+function.getSimpleName()+function.args+location;
		
	}

	/**
	 * Tries to find a function among candidates that has arguments as specified
	 * in the typed argument list
	 * @param candidates
	 * @param tal
	 * @return the best implementation found, or null if there is no match.
	 */
	public static Function getImplementation(SourceContext context,
			List<Function> candidates, TypedArgumentList tal) {
		
		if(candidates.isEmpty()) {
			return null;
		}
		
		// FIXME this is one heck of a dirty hack.
		Type.resolveCheckEnabled = false;
		
		if(tal == null) {
			// This is actually legal, in case we don't care about the arguments,
			// we just choose the first one
			return candidates.get(0);
		}
		
	    Function bestMatch = null;
	    int bestScore = -1;
	    for(Function candidate: candidates) {
	    	
	        int score = 0;
	        if(candidate.args.list.size() != tal.list.size()) {
	            continue; // Not qualifying.
	        }
	        int numArgs = Math.min(candidate.args.list.size(), tal.list.size());
	        score += numArgs * 10;
			for(int i = 0; i < numArgs; i++) {
			    Variable funcVar = candidate.args.list.get(i);
			    Variable argVar = tal.list.get(i);
			    if(funcVar.type.equals(argVar.type)) {
			        score += 10;
			    }
			}
			if(candidate.clazz != null) {
			    score += candidate.clazz.getLevel(context);
			}
			if(score > bestScore) {
			    bestScore = score;
			    bestMatch = candidate;
			}
	    }
	
	    Type.resolveCheckEnabled = true;
	    
	    return bestMatch;
	    
	}

	@Override
	public void setComment(OocDocComment oocDocComment) {

		this.comment = oocDocComment;
		
	}

}
