package org.ooc.compiler;


/**
 * Contain versioning information about the compiler.
 * 
 * @author Amos Wenger
 */
public class CompilerVersion {

	/** The major version number */
	public static int major;
	
	/** The minor version number */
    public static int minor;
    
    /** A release isn't fun without a codename */
    public static String codename;
    
    /** The build date */
    public static String date;
    
    /**
     * Print a line of information about the compiler
     */
	public static void printVersion() {

		Object prop = System.getProperty("ooc.version");
		if(prop == null) {
			prop = CompilerVersion.class.getPackage().getImplementationVersion();
		}
		
		System.out.println("# ooc v"+prop);
		
	}
	
}
