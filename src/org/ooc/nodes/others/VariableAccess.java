package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.clazz.StaticMemberAccess;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * Access to a variable, e.g. "myVariable'
 * @see MemberAccess
 * @see StaticMemberAccess
 *
 * @author Amos Wenger
 */
public class VariableAccess extends SyntaxNodeList implements Typed {

    protected Variable variable;

    /**
     * Default constructor
     * @param location
     * @param variable
     */
    public VariableAccess(FileLocation location, Variable variable) {
    	
        super(location);
        this.variable = variable;
        
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
    	if(getParent() == null) {
    		return; // Boarf. Later.
    	}
    	
    	if(getNext() instanceof Dot) {
    		return; // That's suspicious. Is there a MemberFunctionCall hiding?
    	}
            	
        freeze(manager);
        
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	
    	writeWhitespace(a);
        a.append(variable.getName());
        
    }

    public Type getType() {
        return variable.type;
    }
    
    
	@Override
	public String getDescription() {
    	return getClass().getSimpleName()+" #"+hash+" to "+toString()+location;
    }

    
	@Override
	public boolean equals(Object object) {
		
		if(object instanceof Variable) {
			return variable.equals(object);
		}
		
		return super.equals(object);
		
	}

}
