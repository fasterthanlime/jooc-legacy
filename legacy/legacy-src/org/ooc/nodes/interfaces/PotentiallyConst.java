package org.ooc.nodes.interfaces;

/**
 * Implemented by all things which can be const, such as.. variable declarations !
 * 
 * @author Amos Wenger
 */
public interface PotentiallyConst {

	/**
	 * sets the constness of a field
	 * @param isConst
	 */
	public void setConst(boolean isConst);
	
	/**
	 * @return true if this field is const. It means that it cannot be assigned.
	 * It's the correct way to declare a constant in ooc. Every other usage of const
	 * which is typical in C/C++ will yield a compilation error. It is not yet decided
	 * which is the preferred way to do things such as "specify this argument won't
	 * be modified", etc., in ooc (ie. whether to favor the C++ way or the Java way,
	 * or.. another way ?)
	 */
	public boolean isConst();

}
