package org.ooc.frontend.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.PathList;
import org.ooc.frontend.compilers.AbstractCompiler;
import org.ooc.libs.DistLocator;
import org.ooc.libs.SdkLocator;

public class BuildParams {

	public AbstractCompiler compiler = null;
	
	public File distLocation = DistLocator.locate();
	public File sdkLocation = SdkLocator.locate();
	
	public final PathList sourcePath = new PathList();
	public final PathList libPath = new PathList();
	public final PathList incPath = new PathList();
	
	public File outPath = new File("ooc_tmp");
	
	// Path of the text editor to run when an error is encountered in an ooc file 
	public String editor = "";
	
	// Remove the ooc_tmp/ directory after the C compiler has finished
	public boolean clean = true;
	
	// Add debug info to the generated C files (e.g. -g switch for gcc)
	public boolean debug = false;
	
	// Displays which files it parses, and a few debug infos
	public boolean verbose = false;
	
	// More debug messages
	public boolean veryVerbose = false;
	
	// Displays [ OK ] or [FAIL] at the end of the compilation
	public boolean shout = false;
	
	// If false, output .o files. Otherwise output exectuables
	public boolean link = true;
	
	// Run files after compilation
	public boolean run = false;
	
	// Display compilation times for all .ooc files passed to the compiler
	public boolean timing = false;
	
	// Compile once, then wait for the user to press enter, then compile again, etc.
	public boolean slave = false;

	// Should link with libgc at all.
	public boolean enableGC = true;
	
	// link dynamically with libgc (Boehm)
	public boolean dynGC = false;
	
	// add #line directives in the generated .c for debugging.
	// depends on "debug" flag
	public boolean lineDirectives = true;
	
	// either "32" or "64"
	public String arch = "";
	
	// maximum number of rounds the {@link Tinkerer} will do before blowing up.
	public int blowup = 256;
	
	public List<String> dynamicLibs = new ArrayList<String>();
	
}
