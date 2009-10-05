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
	
	public String editor = "";
	
	public boolean clean = true;
	public boolean debug = false;
	public boolean verbose = false;
	public boolean veryVerbose = false;
	public boolean shout = false;
	public boolean link = true;
	public boolean run = false;
	public boolean timing = false;
	public boolean slave = false;
	public boolean enableGC = true; // Should link with libgc at all.
	public boolean dynGC = false; // Should link dynamically with libgc (Boehm)
	
	public String arch = "";
	
	public List<String> dynamicLibs = new ArrayList<String>();

	public int blowup = 256; /** maximum number of rounds the {@link Tinkerer} will do before blowing up. */
	
}
