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
	WIN,
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
				instance = Target.WIN;
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
		
		switch(this) {
		case WIN:
			return "win" + getArch();
		case LINUX:
			return "linux" + getArch();
		case SOLARIS:
			return "solaris" + getArch();
		case HAIKU:
			return "haiku" + getArch();
		default:
			return super.toString();
		}
		
	}
	
}
