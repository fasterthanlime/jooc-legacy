package org.ooc.nodes.array;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * An array declaration
 *
 * @author Amos Wenger
 */
public class ArrayDecl extends VariableDecl {

    private Subscript subscript;
    private Type type;
	private Type baseType;

	/**
	 * Default constructor
	 * @param location
	 * @param variable
	 * @param subscript
	 */
    public ArrayDecl(FileLocation location, Variable variable, Subscript subscript) {
        super(location, variable);
        this.subscript = subscript;
        this.baseType = variable.type;
        this.type = variable.type.deriveArrayLevel(1);
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
        super.writeToCSource(a);
        a.append("[");
        for(SyntaxNode node: this.subscript.nodes) {
            node.writeToCSource(a);
        }
        a.append("]");
    }
    
    
    @Override
	public Type getType() {
    	return type;
    }
    
    /**
     * The base type for an array, e.g. for int[], the base type is int.
     * @return
     */
    public Type getBaseType() {
    	return baseType;
    }
    
    
    @Override
	protected void assembleImpl(AssemblyManager manager) {

    	subscript.assembleAll(manager);
    
    }
    

}
