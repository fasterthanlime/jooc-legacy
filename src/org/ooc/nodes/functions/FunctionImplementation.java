package org.ooc.nodes.functions;

import org.ooc.errors.AssemblyManager;
import org.ubi.FileLocation;

/**
 * Implementation of a function, via the 'implement' keyword
 *
 * @author Amos Wenger
 */
public class FunctionImplementation extends FunctionOverride {

	/**
	 * Default constructor
	 * @param location
	 * @param name
	 */
    public FunctionImplementation(FileLocation location, String name) {
        super(location, name);
    }

    
    @Override
	public void assembleImpl(AssemblyManager manager) {
    	
        super.assembleImpl(manager);
        
        if(!zuperFunc.isAbstract) {
        	manager.queue(this, "Trying to implement concrete function "
        			+" "+zuperFunc.getSimplePrototype()+". Use the override keyword for that");
        	return;
        }
        
    }

    
    @Override
	protected String getNameDesc() {
        return "implementation";
    }

    
    @Override
	protected String getVerbDesc() {
        return "implement";
    }
    
}
