package org.ooc.nodes.interfaces;

/**
 * Used for function definitions
 * @see {@link StaticKeyword}
 * @author Amos Wenger
 */
public interface PotentiallyStatic {

	/**
	 * Set the staticness of a field (variable/function)
	 * @param isStatic
	 */
    public void setStatic(boolean isStatic);
    
    /**
     * @return true if the field (variable/function) is static. "static" means
     * that it belongs to a Class, and "non-static" means that it belongs to
     * an instance of that Class. So, all instances of a class share their
     * static variables, but they all have their own non-static variables.
     */
    public boolean isStatic();
    
}
