package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.TrioFeature;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Function;

/**
 * Handle constructs like:
 * <code>func1().func2()</code>
 * 
 * @author Amos Wenger
 */
public class CascadeCallFeature extends TrioFeature<Typed, Dot, Name> {

	/**
	 * Default constructor
	 */
	public CascadeCallFeature() {
		
		super(Typed.class, Dot.class, Name.class);
		
	}

	@Override
	protected void applyImpl(AssemblyManager manager, Typed typed, Dot dot, Name name) {

		Type type = typed.getType();
		if(type.getArrayLevel() != 0 || type.getPointerLevel() != 0) {
			return;
		}
		ClassDef def = manager.getContext().getClassDef(type.name);
		if(def != null) {
			ClassDef classDef = def.clazz.getClassDef();
			Function func = classDef.getImplementation(manager.getContext(), name.content,
					new TypedArgumentList(name.location));
			if(func != null) {
				FunctionCall call = new FunctionCall(name.location, name.content);
				call.impl = func;
				name.replaceWith(manager, call);
			}
		}
		
	}

}
