package org.ooc.structures;

import java.io.IOException;

import org.ooc.nodes.interfaces.PotentiallyStatic;

/**
 * A field is a member function or a member variable of a class.
 * 
 * @author Amos Wenger
 */
public interface Field extends PotentiallyStatic {
	
	/**
	 * Classes' field should be writeable.
	 * @param a
	 * @throws IOException 
	 * @return true if something has been written, false else.
	 */
	public boolean writeDeclaration(Appendable a, Clazz destClazz) throws IOException;

}
