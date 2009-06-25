package org.ooc.compiler;


/**
 * Contain versioning information about the compiler.
 * 
 * @author Amos Wenger
 */
public class Version {

	/** The major version number */
	public static int major;
	
	/** The minor version number */
    public static int minor;
    
    /** A release isn't fun without a codename */
    public static String codename;
    
    /** The build date */
    public static String date;
    
    static {
    	
    	/*
    	Properties props = new Properties();
    	try {
    		// This property file is written by ant at each build, see create-dist-jar.xml
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("org/ooc/compiler/version.properties"));

			major = Integer.parseInt(props.getProperty("major"));
			minor = Integer.parseInt(props.getProperty("minor"));
			codename = props.getProperty("codename");
			date = props.getProperty("date");

		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
    	
    }
    
    /**
     * Print a line of information about the compiler
     */
	public static void printVersion() {

		//System.out.println("# ooc v"+major+"."+minor+" codename "+codename+", built on "+date);
		System.out.println("# ooc v"+System.getProperty(("ooc.version")));
		
	}
	
}
