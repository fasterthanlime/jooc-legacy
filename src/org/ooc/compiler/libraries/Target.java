package org.ooc.compiler.libraries;


/**
 * Used to represent the target platform/architecture for which we're building.
 * 
 * @author Amos Wenger
 */
public enum Target {
	
	/** various GNU/Linux */
	LINUX,
	/** Win 9x, NT, MinGW, etc.*/
	WIN32,
	/** Solaris, OpenSolaris */
	SOLARIS,
	/** Haiku */
	HAIKU;

	static Target instance;
	
	/**
	 * @return a guess of the platform/architecture we're building on 
	 */
	public static Target guessHost() {
		
		if(instance == null) {
		
			String os = System.getProperty("os.name");
			os = os.toLowerCase();
			
			if(os.contains("windows")) {
				instance = Target.WIN32;
			} else if(os.contains("linux")) {
				instance = Target.LINUX;
			} else if(os.contains("sunos")) {
				instance = Target.SOLARIS;
			} else {
				System.err.println("Unknown operating system: '"+os+"', assuming Linux..");
				instance = Target.LINUX; // Default
			}
			
		}
		
		return instance;
		
	}
	
	@Override
	public String toString() {
		
		switch(this) {
		case WIN32:
			return "win32";
		case LINUX:
			return "linux";
		case SOLARIS:
			return "solaris";
		case HAIKU:
			return "haiku";
		default:
			return super.toString();
		}
		
	}
	
}
