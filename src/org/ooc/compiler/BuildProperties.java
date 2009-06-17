package org.ooc.compiler;

import java.util.ArrayList;
import java.util.List;

import org.ooc.backends.Backend;
import org.ooc.backends.BackendFactory;
import org.ooc.compiler.pkgconfig.PkgInfo;

/**
 * Contains the user settings for a particular build session.
 * Mostly the source path and output path, the content provider,
 * the backend, and other options.
 * 
 * If the OOC_DIST environment variable is set, the compiler
 * will determine itself the path to the sdk and to the static libs
 * (e.g. the gc)
 * 
 * @author Amos Wenger
 */
public class BuildProperties {

	/**
	 * @see org.ooc.compiler.ContentProvider
	 */
	public ContentProvider provider;
	
	/**
	 * @see org.ooc.backends.Backend
	 */
    public Backend backend;
    
    /**
     * Enable verbose message output of the builder.
     */
    public boolean isVerbose;
    
    /**
     * A list of directories where .ooc files can be found.
     * Comparable to Java's classpath. However, as the 'package' directive
     * doesn't exists in ooc, to avoid information duplication, the sourcePath
     * is crucial when building !
     */
    public SourcePath sourcePath;
    
    /**
     * All .c/.h files outputted will be relative to outPath, in
     * the right folder according to their packages.
     */
    public String outPath;
    
    /** 
	 * The default output path. Note that the default behavior of "instant"
	 * backends
	 */
	public static final String DEFAULT_OUTPATH = "ooc_tmp";
    
    /**
     * Some libraries (such as the Boehm GC, ie. libgc.a)
     * need to be statically linked to our executables.
     * The library path should contain directories like
     * win32, linux, etc. for each operating system/architecture
     * where static (.a) versions of these libraries can be found.
     * The default distribution includes precompiled libraries
     * for a few platforms already. Defaults to "."
     * Equivalent to gcc's "-L"
     */
	public final List<String> libPath;
    
    /**
     * Where can we find header files? Used e.g. by the Make and GCC
     * backends to pass to the real C compiler. Defaults to "."
     * Equivalent to gcc's "-I"
     */
    public final List<String> incPath;
    
    /**
     * Currently used by the Make backend to control the relative location
     * of the Makefile and, thus, executables. Defaults to ".."
     */
    public String prefix;

    /**
     * Packages managed by the 'pkgconfig' utility
     */
    public final List<PkgInfo> pkgInfos;
	
	/**
     * Default constructor, initialize all values to default.
     * The library path and the source path are initialized relative
     * to whatever OOC_DIST is set, if it is set. Otherwise, they
     * are empty. 
     */
    public BuildProperties() {
    	
    	sourcePath = new SourcePath();
    	provider = new FileContentProvider(sourcePath);
    	backend = BackendFactory.getBackend("gcc");
    	isVerbose = false;
    	
    	libPath = new ArrayList<String>();
    	incPath = new ArrayList<String>();
    	outPath = DEFAULT_OUTPATH;
    	prefix = "..";
    	
    	pkgInfos = new ArrayList<PkgInfo>();
    	
    	String oocDist = System.getenv("OOC_DIST");
    	if(oocDist != null) {
    		incPath.add(oocDist+"/libs/headers");
    		libPath.add(oocDist+"/libs/");
    		sourcePath.add(oocDist+"/sdk/");
    	}
    	
    }
	
}
