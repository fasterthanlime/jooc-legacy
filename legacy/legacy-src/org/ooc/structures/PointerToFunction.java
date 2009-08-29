package org.ooc.structures;

import java.util.ArrayList;

import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;

/**
 * A fake function, needed to treat function pointers as real functions. 
 * 
 * @author Amos Wenger
 */
public class PointerToFunction extends Function {

	protected VariableAccess access;

	/**
	 * Default constructor
	 * @param access
	 */
	public PointerToFunction(VariableAccess access) {
		
		super("<pointer to function>", Type.UNRESOLVED, null, new TypedArgumentList(
				access.location, new ArrayList<Variable>()));
		this.access = access;

	}
	
	
	@Override
	public String getSimpleName() {
	
		return access.toString();
		
	}
	
	
	@Override
	public String getMangledName(Clazz destClazz) {
	
		return getSimpleName();
		
	}
	
	
	@Override
	public String getMangledName() {
	
		return getSimpleName();
		
	}

}
