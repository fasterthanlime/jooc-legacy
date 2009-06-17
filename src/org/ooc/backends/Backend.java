package org.ooc.backends;

import org.ooc.compiler.BuildProperties;

/**
 * A backend manages the build from production of .ooc files to its
 * low-level compilation by a C compiler (e.g. gcc) or the creation
 * of a Makefile for later building.
 * 
 * Backends can be classified into two categories:
 * <ul>
 * 	<li>"Direct" backends: they should output executable files directly,
 * by invocation of a C compiler and linker. In other words, the program
 * built should be directly usable after the build. See GccBackend for
 * an example of a direct backend.</li>
 * 	<li>"Indirect" backends: they output a set of file, which can be built
 * at a later time, without need of the ooc compiler. It is particularly useful
 * where pure C source is needed, e.g. if you're writing a library. See
 * MakeBackend for an example of indirect backend.</li>
 * </ul>
 * 
 * Backends generally make a distinction between "modules" and "executables".
 * Basically, an executable is a module which has a main() method.
 * 
 * @author Amos Wenger
 */
public abstract class Backend {
	
	/**
	 * Create a new Backend.
	 * @param params These params are backend-specific. All non-specific parameters
	 * should be handled by the CommandLineInterface and/or CompilerDaemon instead.
	 */
	protected Backend(String params) {}

	/**
	 * 
	 * @param info
	 * @param props
	 * @return
	 * @throws Exception mostly IO exceptions, maybe exceptions while writing C code
	 * (ie. bugs in the Compiler)
	 */
	public abstract int compile(ProjectInfo info, BuildProperties props) throws Exception;

}
