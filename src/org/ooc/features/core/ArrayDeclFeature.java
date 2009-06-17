package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.CoupleFeature;
import org.ooc.nodes.array.ArrayDecl;
import org.ooc.nodes.array.Subscript;
import org.ooc.nodes.others.VariableDecl;

/**
 * Recognize array declarations 
 * 
 * @author Amos Wenger
 */
public class ArrayDeclFeature extends CoupleFeature<VariableDecl, Subscript> {
	
	/**
	 * Default constructor
	 */
	public ArrayDeclFeature() {
		super(VariableDecl.class, Subscript.class);
	}

	@Override
	protected void applyImpl(AssemblyManager manager, VariableDecl decl, Subscript sub) {
		
		if(manager.isDirty(sub)) {
    		return;
    	}
		
		decl.replaceWith(manager, new ArrayDecl(decl.location, decl.variable, sub));
        sub.drop();
		
	}	
	
}
