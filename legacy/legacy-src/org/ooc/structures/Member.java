package org.ooc.structures;

import java.io.IOException;

import org.ooc.nodes.others.Initialization;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.nodes.types.Type;

/**
 *
 * @author Amos Wenger
 */
public class Member implements Field {

    protected VariableDecl variableDecl;
	protected Clazz clazz;

    /**
     * Default constructor
     * @param variable
     * @param init
     */
    public Member(VariableDecl variable, Clazz clazz) {
        this.variableDecl = variable;
        this.clazz = clazz;
    }
    
    
    public boolean writeDeclaration(Appendable a, Clazz destClazz) throws IOException {
    	
    	if(variableDecl.variable.isStatic) {
    		a.append("extern ");
    	}
    	
    	String fullName = variableDecl.variable.getName(destClazz);
		Type type = variableDecl.getType();
		type.writeDeclaration(a, fullName);
    	
    	return true;
    	
    }
    
    /**
     * Get the variable declaration associated to this member
     * @return
     */
    public VariableDecl getVariableDecl() {
    	
		return variableDecl;
		
	}

	
	public boolean isStatic() {

		return variableDecl.isStatic();
		
	}

	
	public void setStatic(boolean isStatic) {

		variableDecl.setStatic(isStatic);
		
	}

	/**
	 * Write the definition of this member variable (to a C source, usually)
	 * @param a
	 * @throws IOException
	 */
	public void writeDefinition(Appendable a) throws IOException {

		variableDecl.writeWhitespace(a);
		variableDecl.variable.type.writeDeclaration(a, variableDecl.variable
				.getName(clazz));
		
		Initialization init = variableDecl.getInitialization();
		
		if(init != null) {
			a.append(" = ");
			init.getValue().writeToCSource(a);
		}
		
	}

}
