package org.ooc.backends.compilers;

/**
 * Options for Gcc4
 * 
 * @author Amos Wenger
 */
public class Gcc4 implements AbstractCompiler {

	@Override
	public String getC99() {
		return "-std=c99";
	}

	@Override
	public String getDynamicLibrary(String libName) {
		return "-l" + libName;
	}
	
	@Override
	public String getIncludePath(String includePath) {
		return "-I" + includePath;
	}

	@Override
	public String getLibraryPath(String libraryPath) {
		return "-L" + libraryPath;
	}

	@Override
	public String getOutputFile(String outputFile) {
		return "-o" + outputFile;
	}
	
}
