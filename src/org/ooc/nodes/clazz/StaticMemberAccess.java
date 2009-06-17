package org.ooc.nodes.clazz;

import java.io.IOException;

import org.ooc.nodes.others.VariableAccess;
import org.ooc.structures.Clazz;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * Access to a static member, ie. "MyClass.myStaticField" 
 * @see MemberAccess
 * @see VariableAccess
 * 
 * @author Amos Wenger
 */
public class StaticMemberAccess extends VariableAccess {

	private Clazz clazz;

	/**
	 * Default constructor
	 * @param location
	 * @param clazz
	 * @param variable
	 */
	public StaticMemberAccess(FileLocation location, Clazz clazz, Variable variable) {
		
		super(location, variable);
		this.clazz = clazz;
		if(clazz == null) {
			throw new Error("Heeeeeeeelp! initialized StaticMemberAccess with a null clazz !");
		}
		
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		writeWhitespace(a);
		a.append(variable.getName(clazz));
		
	}
	
}
