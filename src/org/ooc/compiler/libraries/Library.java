package org.ooc.compiler.libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.nodes.libs.Use;

/**
 * Provides abstraction for includes, dynamic and static linking of
 * libraries, in a cross-platform way.
 * @see Use
 * @see LibraryManager
 * 
 * @author Amos Wenger
 */
public class Library {

	/**
	 * The name of the library, as referred in a 'use' directive, e.g. "gl",
	 * "glu", "glut", "gc", or "pthread"
	 * @see Use
	 */
	public final String name;
	
	/**
	 * The name of the "real" libraries, e.g. "gc" for "libgc.a/libgc.so"
	 */
	public final Map<Target, String> libNames;
	
	/**
	 * Names of dependencies. These names are as referred in 'use' directives.
	 * Example: "glut" depends on "glu" and "gl"
	 */
	public final List<String> deps;
	
	/**
	 * The name of include needed for this library, e.g. "GL/GL.h", "GL/glut.h"
	 */
	public final List<String> incs;
	
	/**
	 * If true, the library will be linked statically, if it can be found,
	 * or it will tried to be linked dynamically, and if not found, fail.
	 * 
	 * By default, libraries are dynamically linked, as it's easier and often
	 * the preferred way, nowadays.
	 */
	public boolean staticByDefault;

	/**
	 * Packages managed by the 'pkgconfig' utility
	 */
	public final List<String> pkgNames;
	
	/**
	 * Default constructor
	 * @param name
	 */
	public Library(String name) {

		this.name = name;
		libNames = new HashMap<Target, String>();
		deps = new ArrayList<String>();
		incs = new ArrayList<String>();
		staticByDefault = false;
		pkgNames = new ArrayList<String>();
		
	}
	
}
