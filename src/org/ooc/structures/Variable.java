package org.ooc.structures;

import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * A variable, its type and its name.
 *
 * @author Amos Wenger
 */
public class Variable {

	/**
	 * The type of our variable, e.g. int, MyClass, etc.
	 */
    public Type type;
    
    /**
     * Our variable's name, e.g. "myVariable"
     */
    protected String name;
    
    /**
     * {@link StaticKeyword} 
     */
    public boolean isStatic;

    /**
     * {@link ConstKeyword}
     */
	public boolean isConst;


    /**
     * Default constructor for a non-static variable, non-const variable
     * @param type
     * @param name
     */
    public Variable(Type type, String name) {
    	
        this(type, name, false);
        
    }
    
    /**
     * Default constructor for a non-const variable 
     * @param type
     * @param name
     * @param isStatic
     */
    public Variable(Type type, String name, boolean isStatic) {
    	
    	this(type, name, isStatic, false);
    	
    }
    
    /**
     * Default constructor for a variable
     * @param type
     * @param name
     * @param isStatic
     */
    public Variable(Type type, String name, boolean isStatic, boolean isConst) {
    	
        this.type = type;
        this.name = name.trim();
        this.isStatic = isStatic;
        this.isConst = isConst;
        
        if(type == null) {
        	throw new Error("Instantiated Variable with null type !");
        }
        
    }

	
    @Override
	public String toString() {
		
        if(name.isEmpty()) {
            return type.toString();
        }
        
        if(type.getPointerLevel() > 0) {
        	return type.toString()+name;
        }
        	
        if(type.getArrayLevel() == 1) {
        	return type.name+" "+name+"[]";
        }
        
    	return type.toString()+" "+name;
		
    }

	/**
	 * @param destClazz The class this variable is supposed to be in.
	 * @return the full name of this variable, e.g. "MyClass_myVariable" if
	 * it's static, or just "myVariable" if it's not.
	 * @throws IOException
	 */
	public String getName(Clazz destClazz) {

		if(isStatic && destClazz != null) {
			return destClazz.underName + SyntaxNode.SEPARATOR + name;
		}
		
		return name;
		
	}
	
	/**
	 * @return the full name of this variable
	 */
	public String getName() {
	
		return getName(null);
		
	}


	/**
	 * Create a new function pointer
	 * @param location
	 * @return a function pointer 
	 */
	public PointerToFunction newFunctionPointer(FileLocation location) {

		return new PointerToFunction(new VariableAccess(location, this));
		
	}

}