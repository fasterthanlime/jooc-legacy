package org.ooc.nodes.reference;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.structures.Function;
import org.ooc.structures.PointerToFunction;
import org.ubi.FileLocation;

/**
 * Reference to a static function.
 * @see FunctionReference
 * 
 * @author Amos Wenger
 */
public class StaticFunctionReference extends FunctionReference {

	protected ClassDef classDef;

	/**
	 * Default construtor
	 * @param location
	 * @param classDef
	 * @param name
	 */
	public StaticFunctionReference(FileLocation location, ClassDef classDef, String name) {
		super(location, name);
		this.classDef = classDef;
	}
	
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		Function impl = classDef.getImplementation(manager.getContext(), name);
		if(impl == null) {
			manager.queue(this, "No implementation found for '"+name+"'");
			return;
		} else if(!impl.isStatic && !(impl instanceof PointerToFunction)) {
			manager.queue(this, "Can't reference non-static function '"+name+"'");
			return;
		}
		this.impl = impl;
		
	}
	
	/**
	 * @return the class definition of this function reference
	 */
	public ClassDef getClassDef() {
		return classDef;
	}

}
