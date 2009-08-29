package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.functions.FunctionDef;

/**
 * A feature which inserts initialization to null for all pointers, e.g. it's
 * better to have a NullPointerException than a dangling pointer and a segfault
 * 
 * @author Amos Wenger
 */
public class PointerInitializationFeature extends SingleFeature<FunctionDef> {

	/**
	 * Default constructor
	 */
	public PointerInitializationFeature() {
		super(FunctionDef.class);
	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, FunctionDef funcDef) {
		
		if(funcDef.function.isNamed("new")) {
			
			// blah.
			
		}
		
	}	

}
