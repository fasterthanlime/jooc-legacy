package org.ooc.nodes.functions;

import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.structures.Clazz;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * The override of a function, with the 'override' keyword.
 *
 * @author Amos Wenger
 */
public class FunctionOverride extends FunctionDef {

    protected Function zuperFunc;

    /**
     * Default constructor
     * 
     * @param location
     * @param name
     */
    public FunctionOverride(FileLocation location, String name) {
        super(location, new Function(name, null, null, new TypedArgumentList(location, new ArrayList<Variable>()))); // Ooh is it ugly? Yes it is.
    }

    
    @Override
	public void assembleImpl(AssemblyManager manager) {
    	
    	if(getParent() == null) {
    		manager.queue(this, "Here "+getClass().getSimpleName()+", null parent, returning...");
    		return;
    	}
    	
        ClassDef classDef = getParent().getNearest(ClassDef.class);
        if(classDef == null) {
            manager.err("Expected an "+getNameDesc()+"  only in a class definitino, not in a "+getParent().getClass().getSimpleName(), this);
            return;
        }

        Clazz destClazz = classDef.clazz;
        Clazz zuperClazz = destClazz.getZuperOrNull(manager.getContext());
        if(zuperClazz == null) {
            manager.queue(this, "Trying to "+getVerbDesc()+" ["+function.getSimpleName()
            		+"(...)] in class ["+destClazz.fullName+"] which has no super-class.");
            return;
        }
        List<Function> funcs = zuperClazz.getUnmangledFunctionsRecursive(manager.getContext(), function.getSimpleName());
        if(funcs.isEmpty()) {
            manager.errAndFail("Trying to " + getVerbDesc() + " unexisting function "
            		+ function.getSimpleName() + " in " + destClazz.fullName
            		+ ", but no luck!", this);
            return;
        }
        // FIXME this is _not_ a proper way of knowing for sure which function we're overriding
        this.zuperFunc = funcs.get(0);
        
        FunctionDef funcDef = new FunctionDef(location, zuperFunc.copyInClass(destClazz));
        funcDef.function.returnType = zuperFunc.returnType;
        funcDef.function.isAbstract = false;
        funcDef.addAll(this);
        replaceWith(manager, funcDef);
        freeze(manager);
        
    }

    protected String getNameDesc() {
        return "override";
    }

    protected String getVerbDesc() {
        return "override";
    }

}