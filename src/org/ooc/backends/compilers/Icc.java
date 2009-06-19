package org.ooc.backends.compilers;

/**
 * Options for the Intel C++ compiler on Windows 
 * 
 * @author Amos Wenger
 */
public class Icc implements AbstractCompiler {

	@Override
	public String getC99() {
		return "/Qstd=c99";
	}

	@Override
	public String getDynamicLibrary(String libName) {
		return libName + ".lib";
	}
	
	@Override
	public String getIncludePath(String includePath) {
		return "/I\"" + includePath + "\"";
	}

	@Override
	public String getLibraryPath(String libraryPath) {
		return "/L\"" + libraryPath + "\"";
	}

	@Override
	public String getOutputFile(String outputFile) {
		return "/OUT:\"" + outputFile + "\"";
	}	
	
}
