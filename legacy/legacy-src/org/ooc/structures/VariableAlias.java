package org.ooc.structures;

import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * Used to infer type of parameters, e.g.
 * <code>
 * class Mine {
 * 
 *   int value;
 * 
 *   func doStuff(value) {
 *     printf("value is an int, and it equals %d\n", value);
 *   }
 *   
 * } 
 * </code> 
 * 
 * @author Amos Wenger
 */
public class VariableAlias extends Variable {

	/**
	 * Create a new variable alias at default location 
	 * @param name
	 * @param location
	 */
	public VariableAlias(String name, FileLocation location) {
		
		super(new Type(location, null, "<alias of "+name+">"), name);
		
	}
	
}
