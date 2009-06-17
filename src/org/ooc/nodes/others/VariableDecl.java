package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.interfaces.PotentiallyConst;
import org.ooc.nodes.interfaces.PotentiallyStatic;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * Represents a variable declaration, e.g.:
 * <tt>
 * Object object;
 * </tt>
 *
 * @author Amos Wenger
 */
public class VariableDecl extends SyntaxNode implements Typed, PotentiallyStatic, PotentiallyConst {

	/**
	 * The variable being declared by this node. (Contains the type and the name)
	 */
    public Variable variable;
    
    private Initialization initialization;

	/**
     * Creates a VariableDeclaration to a specified variable.
     * @param location
     * @param variable
     */
    public VariableDecl(FileLocation location, Variable variable) {
    	
        super(location);
        this.variable = variable;
        if(variable.type.isUnknown()) {
        	throw new Error("Instantiated VariableDecl with unknown type !");
        }
        
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	
    	writeToCSource(a, true);
    	
    }
    
    /**
     * Variant of writeToCSource when being aggregated by another class, which
     * wants to have control over whitespace writing.
     * @param a
     * @param writeWhitespace
     * @throws IOException
     */
    public void writeToCSource(Appendable a, boolean writeWhitespace) throws IOException {
    	
    	if(writeWhitespace) {
    		writeWhitespace(a);
    	}
    	if(variable.isConst) {
    		a.append("const ");
    	}
    	if(variable.isStatic) {
    		a.append("static ");
    	}
        variable.type.writeDeclaration(a, variable.getName());
        
    }

    @Override
    protected void assembleImpl(AssemblyManager manager) {

    	variable.type.setContext(getParent());
    	variable.type.assembleForce(manager);

    	SyntaxNodeList parent = getParent();
    	if(parent != null) {
			Scope scope = parent.getNearest(Scope.class);
	        if(scope == null) {
	            manager.err("Error, VariableDecl attempting to add a variable to Nearest scope, but no Scope near ! '"
	            		+this+"' hierarchy is "+getParent().getHierarchyRepr(), this);
	            return;
	        }
	        if(!(parent instanceof ClassDef)) { // else, it would be in the Class's members already.
	            scope.addVariable(variable);
	        }
	        
	        ClassDef def = parent.getNearest(ClassDef.class);
	        if(def != null && def.clazz.isNamed(variable.type.name)) {
	        	variable.type.name = def.clazz.underName;
	        }
    	}
        
        if(variable.type.isResolved) {
        	if(!variable.type.isValid && variable.type.clazz == null
        			&& Character.isLowerCase(variable.type.name.charAt(0))) {
        		manager.errAndFail("Invalid type name "+variable.type.name
        				+". Types must start with an upper-case letter", this);
        	}
        	lock();
        } else {
        	manager.queue(this, "Type '"+variable.type.name+"' can't be resolved.");
        }

    }
    
    @Override
	public String getDescription() {
    	
    	return toString()+location;
    	
    }

	@Override
	public Type getType() {
		
		return variable.type;
		
	}
	
	@Override
	protected boolean isSpaced() {
		
		return true;
		
	}

	@Override
	public void setStatic(boolean isStatic) {

		variable.isStatic = isStatic;
		
	}

	@Override
	public boolean isStatic() {
		
		return variable.isStatic;
		
	}
	
	@Override
	public void setConst(boolean isConst) {
		
		variable.isConst = isConst;
		
	}
	
	@Override
	public boolean isConst() {
		
		return variable.isConst;
		
	}

	/**
	 * @return the initialization
	 */
	public Initialization getInitialization() {
		
		return initialization;
		
	}

	/**
	 * @param initialization the initialization to set
	 */
	public void setInitialization(Initialization initialization) {
		
		this.initialization = initialization;
		
	}
    
}