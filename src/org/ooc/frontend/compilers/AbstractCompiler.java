package org.ooc.frontend.compilers;

import java.io.IOException;

/**
 * The doc is relative to gcc, since it's the compiler I'm most familiar with.
 * 
 * @author Amos Wenger
 */
public interface AbstractCompiler {

	public void setExecutable(String executableName);
	
	/** -o option in gcc */
	public void setOutputPath(String path);
	
	/** -I option in gcc */
	public void addIncludePath(String path);
	
	/** -L option in gcc */
	public void addLibraryPath(String path);
	
	/** -l option in gcc */
	public void addDynamicLibrary(String library);
	
	/** -c option in gcc */
	public void setCompileOnly();
	
	/** -g option in gcc */
	public void setDebugEnabled();
	
	/** .o file to link with */
	public void addObjectFile(String path);
	
	/** -D option in gcc */
	public void defineSymbol(String symbolName);
	
	/** -U option in gcc */
	public void undefineSymbol(String symbolName);
	
	/** multiple -arch options in gcc */
	public void setFatArchitectures(String[] archs);
	
	/** -isysroot /Developer/SDKs/MacOSXxxx.sdk -mmacosx-version-min=xxx in gcc */
	public void setOSXSDKAndDeploymentTarget(String version);
	
	/** any compiler-specific option */
	public void addOption(String option);
	
	/** @return the exit code of the compiler */
	public int launch() throws IOException, InterruptedException;

	public boolean supportsDeclInFor();
	public boolean supportsVLAs();
	
	public void reset();

	public String getCommandLine();
	
	public AbstractCompiler clone();
	
}
