package org.ooc.nodes.interfaces;

/**
 * Used for class definitions and function definitions
 * @see {@link AbstractKeyword}
 * @author Amos Wenger
 */
public interface PotentiallyAbstract {

	/**
	 * Set the abstractness state of a class/function.
	 * @param isAbstract
	 */
    public void setAbstract(boolean isAbstract);
    
    /**
     * @return true if this class/function is abstract. An abstract class can't
     * be instanciated, but it can be subclassed. An abstract function can only
     * be in an abstract class, it means that any concrete (non-abstract) class
     * *has* to implement it. An abstract can also provide an implementation
     * of a function (thus, a non-abstract function).
     * @see {@link AbstractKeyword}
     */
    public boolean isAbstract();
    
}
