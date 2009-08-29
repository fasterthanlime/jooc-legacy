package org.ooc.features.checks;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.structures.Function;

/**
 * Check that concrete classes have no abstract functions.
 * 
 * @author Amos Wenger
 */
public class NoAbstractFuncInConcreteClassCheck extends SingleFeature<ClassDef> {

	/**
	 * Default constructor
	 */
	public NoAbstractFuncInConcreteClassCheck() {
		super(ClassDef.class);
	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, ClassDef def) {
		
		if(def.clazz.isAbstract) {
			return; // No obligations for an asbtract class
		}
		
		for(Function func: def.clazz.functions) {
			if(func.isAbstract) {
				manager.errAndFail("Abstract function "+func.getSimplePrototype()+" declared in concrete class " + def.clazz.fullName +
						" (tip: Make the class abstract or implement the function!)", def);
			}
		}
		
	}
	
}
