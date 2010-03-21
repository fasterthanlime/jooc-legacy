package org.ooc.frontend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.compilers.AbstractCompiler;
import org.ooc.libs.DistLocator;
import org.ooc.libs.SdkLocator;
import org.ooc.utils.ReadEnv;

public class BuildParams {

	public AbstractCompiler compiler = null;
	
	/** If no compiler is specified, use gcc by default (or not) */
	public boolean gccByDefault = true;
	
	// if non-null, use 'linker' as the last step of the compile process, with driver=sequence
	public String linker = null;
	
	public File distLocation = DistLocator.locate();
	public File sdkLocation = SdkLocator.locate();
	
	public final PathList sourcePath = new PathList();
	public final PathList libPath = new PathList();
	public final PathList incPath = new PathList();
	
	// FIXME make it portable, make it use the OOC_LIBS env variable too.
	public File libsPath = getLibsPath();
		
	public File outPath = new File("ooc_tmp");
	
	// list of symbols defined e.g. by -Dblah
	public List<String> defines = new ArrayList<String>();
	
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
	public boolean shout = true;
	
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
	
	// if non-null, will create a static library with 'ar rcs <outlib> <all .o files>'
	public String outlib = null;
	
	// add a main method if there's none in the specified ooc file
	public boolean defaultMain = true;
	
	// either "32" or "64"
	public String arch = "";
	
	public String[] fatArchitectures;
	public String osxSDKAndDeploymentTarget;
	
	// maximum number of rounds the {@link Tinkerer} will do before blowing up.
	public int blowup = 256;

	// name of the backend, either "c" or "json".
	public String backend = "c";
	
	// the entry point. for most apps, it will be main,
	// but for libraries, kernels, Win32 apps, etc. it can have another name
	public String entryPoint = "main";
	
	public List<String> dynamicLibs = new ArrayList<String>();
	public List<String> additionals = new ArrayList<String>();
	public List<String> compilerArgs = new ArrayList<String>();

	// how many threads to use with the sequence driver
	public int sequenceThreads = Runtime.getRuntime().availableProcessors();

	/* Builtin defines */
	public static final String GC_DEFINE = "__OOC_USE_GC__";

	private File getLibsPath() {
		String path = ReadEnv.getEnv().get("OOC_LIBS");
		return path == null ? new File("/usr/lib/ooc/") : new File(path);
	}
	
	
	public BuildParams() {
		// use the GC by default =)
		defines.add(GC_DEFINE);
	}
	
	public void defineSymbol(String symbol) {
		if(!defines.contains(symbol)) {
			defines.add(symbol);
		}
	}
	
	public void undefineSymbol(String symbol) {
		defines.remove(symbol);
	}
	
}
