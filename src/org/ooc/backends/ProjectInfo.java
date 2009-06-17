package org.ooc.backends;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.compiler.BuildProperties;
import org.ooc.errors.SourceContext;

/**
 * Project information, more explicitly a set of modules and executables.
 * Used mostly to resolve paths.
 */
public class ProjectInfo {

	/**
	 * Relative out path, e.g. taking the prefix into account
	 */
	public String relativeOutPath;
	
	/**
	 * This project's modules, e.g. sources without a main()
	 */
	public Map<String, SourceContext> modules;
	
	/**
	 * This project's executables, e.g. sources with a main()
	 */
	public Map<String, SourceContext> executables;
	
	/**
	 * Dynamic libraries (ex. libdummy.so)
	 */
	public List<String> dynamicLibraries;
	
	/**
	 * Static libraries (ex. libs/linux/libgc.a)
	 */
	public List<String> staticLibraries;
	
	/**
	 * C++ modules linked to this project
	 */
	public List<File> cppModules;
	
	/**
	 * The build properties
	 */
	public final BuildProperties props;
	
	/**
	 * Default consructor
	 * @param props The build properties
	 */
	public ProjectInfo(BuildProperties props) throws IOException {
		
		String canonicalPrefix = new File(props.outPath, props.prefix).getCanonicalPath();
		String absoluteOutpath = new File(props.outPath).getCanonicalPath();
		
		if(canonicalPrefix.length() >= absoluteOutpath.length()) {
			relativeOutPath = "";
		} else {
			relativeOutPath = absoluteOutpath.substring(canonicalPrefix.length() + 1) + "/";	
		}
		this.props = props;
		
		modules = new HashMap<String, SourceContext>();
		executables = new HashMap<String, SourceContext>();
		dynamicLibraries = new ArrayList<String>();
		staticLibraries = new ArrayList<String>();
		cppModules = new ArrayList<File>();
	}

	/**
	 * Get the relative path of the module with specified extension.
	 * E.g. for moduleName "gtk.Button" and extension "c", with outpath "outPath"
	 * and prefix "../", it will return "gtk/Button.c" (the canonical equivalent
	 * of outPath/../gtk/Button.c)
	 * It's mostly used for includes between header files.
	 * @param moduleName
	 * @param extension
	 * @return
	 */
	public String getRelativePath(String moduleName, String extension) {

		return (relativeOutPath + "/" + moduleName.replace('.', '/') + extension).replace("//", "/");
		
	}
	
	/**
	 * Get the path of the module with specified extension
	 * E.g. for moduleName "gtk.Button" and extension "c", with outpath "outPath"
	 * it will return "outPath/gtk/Button.c"
	 * @param moduleName
	 * @param extension
	 * @return
	 */
	public String getOutPath(String moduleName, String extension) {

		return (props.outPath +  "/" + moduleName.replace('.', '/') + extension).replace("//", "/");
		
	}

	/**
	 * Test for the presence of a source context in this project
	 * @param context
	 * @return true if it's in this project, false otherwise
	 */
	public boolean contains(SourceContext context) {

		return modules.containsValue(context) || executables.containsValue(context);
		
	}

	/**
	 * Add a C++ file to be handled by the ooc compiler
	 * @param cppModule
	 */
	public void addCppModule(File cppModule) {

		cppModules.add(cppModule);
		
	}
	
	/**
	 * Add recursively a source and its dependencies to this project info
	 * @param context
	 * @throws IOException
	 */
	public void addSourceRecursive(SourceContext context) throws IOException {
		
		String baseName = context.source.getInfo().getBaseName();
		
		if(context.hasUnmangledFunction("main")) {
			executables.put(baseName, context);
		} else {				
			modules.put(baseName, context);
		}
		
		for(SourceContext dep: context.getDependencies()) {
			if(!contains(dep)) {
				addSourceRecursive(dep);
			}
		}
	}
	
}
