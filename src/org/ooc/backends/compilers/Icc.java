package org.ooc.backends.compilers;

/**
 * Options for the Intel C++ compiler on Windows 
 * 
 * @author Amos Wenger
 */
public class Icc implements AbstractCompiler {

	
	public String getCommand() {
		return "icc";
	}
	
	
	public String getC99() {
		return "/Qstd=c99";
	}

	
	public String getDynamicLibrary(String libName) {
		return libName + ".lib";
	}
	
	
	public String getIncludePath(String includePath) {
		return "/I\"" + includePath + "\"";
	}

	
	public String getLibraryPath(String libraryPath) {
		return "/L\"" + libraryPath + "\"";
	}

	
	public String getOutputFile(String outputFile) {
		return "/OUT:\"" + outputFile + "\"";
	}	
	
}
