package org.ooc.structures;

import org.ubi.FileLocation;

/**
 * Used for variables in function definitions which are mapped to a member, e.g.
 * <code>
 * class Keeper {
 * 
 *   Secret secret;
 *   
 *   new(=secret);
 *   =secret get();
 *   void set(=secret);
 *   
 * }
 * </code>
 * 
 * @author Amos Wenger
 */
public class AssignedVariableAlias extends VariableAlias {

	/**
	 * Default constructor
	 * @param name
	 * @param location
	 */
	public AssignedVariableAlias(String name, FileLocation location) {
		
		super(name, location);
		
	}

}
