package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.TrioFeature;
import org.ooc.nodes.clazz.ClassReference;
import org.ooc.nodes.clazz.StaticMemberAccess;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.reference.FunctionReference;
import org.ooc.nodes.reference.StaticFunctionReference;
import org.ooc.structures.Variable;

/**
 * Manages references to static functions
 *  
 * @author Amos Wenger
 */
public class StaticReferenceFeature extends TrioFeature<ClassReference, Dot, FunctionReference> {

	/**
	 * Default constructor
	 */
	public StaticReferenceFeature() {
		super(ClassReference.class, Dot.class, FunctionReference.class);
	}
	
	@Override
	protected void applyImpl(AssemblyManager manager, ClassReference classRef,
			Dot dot, FunctionReference funcRef) {

		Variable member = classRef.classDef.getMember(manager.getContext(), funcRef.getName());
		if(member != null) {
			funcRef.replaceWith(manager, new StaticMemberAccess(funcRef.location, classRef.classDef.clazz, member));
		} else {
			funcRef.replaceWith(manager, new StaticFunctionReference(funcRef.location, classRef.classDef, funcRef.getName()));
		}
		classRef.drop();
		dot.drop();
		
	}

}
