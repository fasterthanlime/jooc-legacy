package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.TrioFeature;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.ClassReference;
import org.ooc.nodes.clazz.StaticMemberAccess;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.others.Name;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;

/**
 * Allows access to static fields of class, e.g. "MyClass.myStaticField"
 * 
 * @author Amos Wenger
 */
public class StaticClassAccessFeature extends TrioFeature<ClassReference, Dot, Name> {

	/**
	 * Default constructor
	 */
	public StaticClassAccessFeature() {
		
		super(ClassReference.class, Dot.class, Name.class);
		
	}

	@Override
	protected void applyImpl(AssemblyManager manager, ClassReference classRef,
			Dot dot, Name name) {
		
		ClassDef def = classRef.classDef;
		Function func = def.getImplementation(manager.getContext(), name.content,
				new TypedArgumentList(name.location));
		if(func != null) {
			if(!func.isStatic) {
				manager.queue(classRef, "Trying to call static function '"+name.content
						+"' in class "+classRef.classDef.clazz.fullName+", but it's not static in this class!");
			}
			name.drop();
			dot.drop();
			FunctionCall call = new FunctionCall(name.location, name.content);
			call.impl = func;
			call.clazz = def.clazz;
			classRef.replaceWith(manager, call);
			return;
		}
		
		Variable member = def.getMember(manager.getContext(), name.content);
		if(member != null) {
			name.drop();
			dot.drop();
			classRef.replaceWith(manager, new StaticMemberAccess(name.location,
					def.clazz, member));
			return;
		}
		
		// Queuing the classReference, cause it's what will trigger this feature
		manager.queue(classRef, "Attempt to access to static field '"+name.content+"' in class '"
				+classRef.classDef.clazz.fullName+"', no such field exists!");
		
	}

	
	
}
