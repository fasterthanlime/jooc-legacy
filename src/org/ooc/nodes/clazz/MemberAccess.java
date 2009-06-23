package org.ooc.nodes.clazz;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.functions.MemberFunctionCall;
import org.ooc.nodes.others.Parenthesis;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Clazz;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * Access to a member variable of an object, e.g. "myObject.hisField"
 * @see VariableAccess
 * @see StaticMemberAccess
 * 
 * @author Amos Wenger
 */
public class MemberAccess extends VariableAccess {

    private Variable member;
	private VariableAccess access;

    /**
     * Create a member access from a variable access.
     * @param location
     * @param access
     * @param member
     */
    public MemberAccess(FileLocation location, VariableAccess access, Variable member) {
    	this(location, new Variable(access.getType(), access.toString()), member);
    	this.access = access;
    }
    
    /**
     * Create a member access from a variable
     * @param location
     * @param variable e.g. "myObject" in "myObject.hisField"
     * @param member e.g. "hisField" in "myObject.hisField"
     */
    public MemberAccess(FileLocation location, Variable variable, Variable member) {
        super(location, variable);
        this.member = member;
    }

    @Override
    public Type getType() {
        return member.type;
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	
    	if(member.isStatic) {
    		
    		writeWhitespace(a);
    		a.append(member.getName(variable.type.clazz));
    		
    	} else {
    		
	        super.writeToCSource(a);
	        a.append("->");
	        a.append(member.getName());
	        
    	}
        
    }
    
    @Override
    protected void assembleImpl(AssemblyManager manager) {
    	
    	member.type.assemble(manager);
    	if(member.isStatic) {
    		
    		Clazz clazz = variable.type.clazz;
			if(clazz == null) {
				manager.queue(variable.type, "Should resolve for member access");
    			manager.queue(this, "Attempting to access a static member of something" +
    				" which class couldn't be determined by the compiler..");
    			return;
    		}
    		replaceWith(manager, new StaticMemberAccess(location, clazz, member));
    		return;
    		
    	}
    	
    	super.assembleImpl(manager);
    	
    	SyntaxNode next = getNext();
		if(next instanceof Parenthesis) {
			
			replaceWith(manager, new MemberFunctionCall(location, member.getName(), ((Parenthesis) next).nodes, access));
    		next.drop();
    		
    	} else {
    		
    		//lock();
    		
    	}
    	
    }

}
