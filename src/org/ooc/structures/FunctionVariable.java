package org.ooc.structures;

import org.ooc.nodes.types.Type;

/**
 * A function seen as a variable for assignment to a function pointer
 * 
 * @author Amos Wenger
 */
public class FunctionVariable extends Variable {

	private Function function;

	/**
	 * Default constructor
	 * @param function
	 */
	public FunctionVariable(Function function) {
		
		super(Type.UNRESOLVED, function.getSimpleName());
		this.function = function;
		
	}

	@Override
	public String getName() {
	
		return function.getMangledName(null);
		
	}
	
}
