package org.ooc.nodes.control;

import java.io.IOException;
import java.util.ArrayList;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.others.Block;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A scope is a block of code delimited by brackets {}, which is scoped, e.g.
 * its declarations can't be accessed from the outside. It can contain pretty much
 * anything from variable declarations to function definitions, including function
 * calls, assignments, etc.
 * 
 * Example:
 * <code>
 * int a;
 * foo(a);
 * {
 *   int b;
 *   bar(b);
 * }
 * foo(b); // ERROR: variable b not visible from here
 * </code>
 *
 * @author Amos Wenger
 */
public class Scope extends Block {

    protected ArrayList<Variable> variables;

    /**
     * Default constructor
     * @param location
     */
    public Scope(FileLocation location) {
        super(location);
        this.variables = new ArrayList<Variable>();
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	writeWhitespace(a);
        a.append("{");
        for(SyntaxNode node: nodes) {
            node.writeToCSource(a);
        }
        a.append('\n');
        writeIndent(a, -1);
        a.append("}");
    }

    /**
     * Add a variable to a scope. Scopes hold references to all variables declared
     * in them so that they can resolve reference to variables.
     * @param variable
     * @see getVariable
     */
    public void addVariable(Variable variable) {
    	
    	if(!variables.contains(variable)) {
    		variables.add(variable);
    	}
    	
    }

    /**
     * Search for a variable in this scope and its parent scopes.
     * @param name
     * @return
     */
    public Variable getVariable(String name) {
    	
        for(Variable variable: variables) {
            if(variable.getName().equals(name)) {
                return variable;
            }
        }
        if(getParent() != null) {
	        Scope nearest = getParent().getNearest(Scope.class);
	        if(nearest != null) {
	            return nearest.getVariable(name);
	        }
        }
		return null;
		
    }

    
    @Override
	protected boolean isIndented() {
        return true;
    }

    /**
     * Generates a Variable with type 'type' and a name beginning with 'name'
     * @param type
     * @param name
     * @return
     */
	public Variable generateTempVariable(Type type, String name) {
		
		String candidate = type.name+SEPARATOR+name;
		int i = 0;
		while(getVariable(candidate) != null) {
			candidate = type.name+SEPARATOR+name+(++i);
		}
		
		Variable variable = new Variable(type, candidate);
		addVariable(variable);
		return variable;
		
	}
	
	/**
     * Get an implementation of the function 'name' without caring about
     * its arguments
     * @param context
     * @param name
     * @return the best implementation, or null if not found.
     */
	public Function getImplementation(SourceContext context, String name) {
		
		return getImplementation(context, name, null);
		
	}
	
	/**
     * Get an implementation of the function 'name' with arguments specified in
     * the typed argument list
     * @param context
     * @param name
     * @param tal
     * @return the best implementation, or null if not found.
     */
    public Function getImplementation(SourceContext context, String name, TypedArgumentList tal) {
    	
    	Function impl = null;
    	
    	for(FunctionDef funcDef: getNodesTyped(FunctionDef.class, false)) {
    		Function func = funcDef.function;
    		
    		// FIXME this is wrong! FunctionDef.getImplementation() should be used. (carefully)
    		//if(func.getSimpleName().equals(name) && func.args.list.size() == tal.list.size()) {
    		if(func.getSimpleName().equals(name)) {
    			impl = func;
    			break;
    		}
    	}
    	
    	if(impl == null) {
    		
    		Variable variable = getVariable(name);
    		if(variable != null) {
    			if(variable.type.isFunctionPointer()) {
	    			// FIXME wrong location, but we can't really ask for it as an argument, can we?
	    			return variable.newFunctionPointer(location);
    			}
    		}
    	}
    	
    	if(impl == null) {
    		SyntaxNodeList parent = getParent();
			if(parent != null) {
				Scope parentScope = parent.getNearest(Scope.class);
				if(parentScope != null) {
					return parentScope.getImplementation(context, name, tal);
				}
    		}
    	}
    	
    	return impl;
    	
    }

}