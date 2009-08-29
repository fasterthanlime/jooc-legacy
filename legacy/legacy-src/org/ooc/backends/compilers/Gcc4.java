package org.ooc.backends.compilers;

/**
 * Options for Gcc4
 * 
 * @author Amos Wenger
 */
public class Gcc4 implements AbstractCompiler {

	
	public String getCommand() {
		return "gcc";
	}
	
	
	public String getC99() {
		return "-std=c99";
	}

	
	public String getDynamicLibrary(String libName) {
		return "-l" + libName;
	}
	
	
	public String getIncludePath(String includePath) {
		return "-I" + includePath;
	}

	
	public String getLibraryPath(String libraryPath) {
		return "-L" + libraryPath;
	}

	
	public String getOutputFile(String outputFile) {
		return "-o" + outputFile;
	}
	
}
