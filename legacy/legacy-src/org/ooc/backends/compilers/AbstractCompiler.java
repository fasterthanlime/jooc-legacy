package org.ooc.backends.compilers;

/**
 * How to abstract away compiler behavior differences.
 * 
 * @author Amos Wenger
 */
public interface AbstractCompiler {

	/** Must return the compiler's command */
	public String getCommand();
	
	/** Must return the option to pass to the compiler to compile to C99 */
	public String getC99();
	
	/** Must return the option to pass to the compiler to link with library libName */
	public String getDynamicLibrary(String libName);
	
	/** Must return the option to pass to the compiler to add includePath to the include path */
	public String getIncludePath(String includePath);
	
	/** Must return the option to pass to the compiler to add libraryPath to the library path */
	public String getLibraryPath(String libraryPath);
	
	/** Must return the option to specify the file to output the binary file */
	public String getOutputFile(String outputFile);
	
}
