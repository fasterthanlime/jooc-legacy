package org.ooc.nodes.interfaces;

/**
 * Used for function definitions, with the 'unmangled' keyword
 * @see {@link UnmangledKeyword}
 * @author Amos Wenger
 */
public interface PotentiallyUnmangled {

	/**
	 * Set the mangledness state of something (a function def, probably)
	 * @param isUnmangled
	 */
    public void setUnmangled(boolean isUnmangled);
    
    /**
     * @return true if it's unmangled, e.g. a function definition. A mangled
     * name could be something like "_MyClass_myfunction" while an unmangled
     * name would be "myfunction"
     */
    public boolean isUnmangled();
    
}
