package org.ooc.compiler;

import org.ooc.errors.AssemblyError;

/**
 * Should be implemented by classes interested by assembly errors, e.g. development
 * tools.
 * 
 * @author Amos Wenger
 */
public interface AssemblyErrorListener {

	/**
	 * Called when an assembly error has occured
	 * @param error
	 */
	public void onAssemblyError(AssemblyError error);
	
}
