package org.ooc.frontend;


/**
 * Used to represent the target platform/architecture for which we're building.
 * 
 * @author Amos Wenger
 */
public enum Target {
	
	/** various GNU/Linux */
	LINUX,
	/** Win 9x, NT, MinGW, etc.*/
	WIN,
	/** Solaris, OpenSolaris */
	SOLARIS,
	/** Haiku */
	HAIKU,
	/** Mac OS X */
	OSX;

	static Target instance;
	
	/**
	 * @return a guess of the platform/architecture we're building on 
	 */
	public static Target guessHost() {
		
		if(instance == null) {
		
			String os = System.getProperty("os.name").toLowerCase();
			
			if(os.contains("windows")) {
				instance = Target.WIN;
			} else if(os.contains("linux")) {
				instance = Target.LINUX;
			} else if(os.contains("sunos")) {
				instance = Target.SOLARIS;
			} else if(os.startsWith("mac os x")) {
				instance = Target.OSX;
			} else {
				System.err.println("Unknown operating system: '"+os+"', assuming Linux..");
				instance = Target.LINUX; // Default
			}
			
		}
		
		return instance;
		
	}
	
	/**
	 * @return true if we're on a 64bit arch
	 */
	public static boolean is64() {
	
		return System.getProperty("os.arch").indexOf("64") != -1;
		
	
	}
	
	/**
	 * @return '32' or '64' depending on the architecture
	 */
	public static String getArch() {
	
		return is64() ? "64" : "32";
		
	}
	
	@Override
	public String toString() {
		return toString(getArch());
	}
	
	public String toString(String arch) {
		
		switch(this) {
		case WIN:
			return "win" + arch;
		case LINUX:
			return "linux" + arch;
		case SOLARIS:
			return "solaris" + arch;
		case HAIKU:
			return "haiku" + arch;
		case OSX:
			return "osx" + arch;
		default:
			return super.toString();
		}
		
	}
	
}
