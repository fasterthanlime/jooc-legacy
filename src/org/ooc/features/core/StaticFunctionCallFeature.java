package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.TrioFeature;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.ClassReference;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.operators.Dot;

/**
 * Allows to call static functions from their class name, e.g.
 * <code>
 * MyClass.staticFunction(argument);
 * </code>
 * 
 * @author Amos Wenger
 */
public class StaticFunctionCallFeature extends TrioFeature<ClassReference, Dot, FunctionCall> {

	/**
	 * Default constructor
	 */
	public StaticFunctionCallFeature() {
		super(ClassReference.class, Dot.class, FunctionCall.class);
	}

	@Override
	protected void applyImpl(AssemblyManager manager, ClassReference classRef,
			Dot dot, FunctionCall call) {
		
		ClassDef classDef = classRef.classDef;
		
		call.impl = classDef.getImplementation(manager.getContext(), call.name, new TypedArgumentList(call));
		if(call.impl == null) {
			manager.queue(classRef, "No function "+call.name+call.getArgsRepr()+" in class "+classDef.clazz.fullName);
			return;
		}
		
		if(!call.impl.isStatic) {
			manager.queue(classRef, "Trying to call function "+call.name+call.getArgsRepr()
					+" as a static function, but it's not static!");
			return;
		}
		
		call.clazz = classDef.clazz;
		classRef.drop();
		dot.drop();
		
	}


}
