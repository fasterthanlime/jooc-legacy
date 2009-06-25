package org.ooc.features.checks;

import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.structures.Clazz;
import org.ooc.structures.Function;

/**
 * This feature, as its name suggests, verify that every contract (e.g. abstract
 * function from a parent class) is implemented in concrete classes.
 * 
 * @author Amos Wenger
 */
public class AllContractsImplementedCheck extends SingleFeature<ClassDef> {

	/**
	 * Default constructor
	 * @param type
	 */
	public AllContractsImplementedCheck() {
		super(ClassDef.class);
	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, ClassDef def) {
		
		if(def.clazz.isAbstract) {
			return; // No obligations for an abstract class
		}
		
		List<Function> contracts = new ArrayList<Function>();
		Clazz current = def.clazz.getZuperOrNull(manager.getContext());
		while(current != null) {
			for(Function func: current.functions) {
				if(func.isAbstract) {
					contracts.add(func);
				}
			}
			current = current.getZuperOrNull(manager.getContext());
		}
		
		for(Function func: contracts) {
			Function impl = def.getImplementation(manager.getContext(), func.getSimpleName(), func.args);
			if(impl == null || impl.isAbstract) {
				manager.queue(def, "Concrete class "+def.clazz.fullName
						+" should implement function "+func.getSimplePrototype());
			}
		}

	}

}
