package org.ooc.frontend;


/**
 * Contain versioning information about the compiler.
 * 
 * @author Amos Wenger
 */
public class CompilerVersion {

	/**
     * Print a line of information about the compiler
     */
	public static void printVersion() {

		Object prop = System.getProperty("ooc.version");
		if(prop == null) {
			prop = CompilerVersion.class.getPackage().getImplementationVersion();
		}
		
		System.out.println("ooc v"+prop);
		
	}
	
}
