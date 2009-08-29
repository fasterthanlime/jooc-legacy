package org.ooc.compiler.libraries;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ooc.backends.ProjectInfo;
import org.ooc.compiler.BuildProperties;
import org.ooc.compiler.pkgconfig.PkgConfigFrontend;
import org.ooc.nodes.libs.Include;
import org.ooc.nodes.libs.Use;
import org.ooc.nodes.libs.Include.IncludePosition;
import org.ooc.nodes.libs.Include.IncludeType;

/**
 * The library manager is at the core of the "use" magic.
 * @see Use
 * @see Library
 * 
 * @author Amos Wenger
 */
public class LibraryManager {

	protected Map<String, Library> libs;
	
	/**
	 * Default constructor.
	 */
	public LibraryManager() {

		//TODO should read library definitions from text files!
		libs = new HashMap<String, Library>();
		
		{
			Library gl = new Library("gl");
			gl.incs.add("GL/gl.h");
			// FIXME repetitions should be avoided by using bitfields instead of an enum
			gl.libNames.put(Target.LINUX, "GL");
			gl.libNames.put(Target.SOLARIS, "GL");
			gl.libNames.put(Target.WIN, "opengl" +  Target.getArch());
			libs.put(gl.name, gl);
		}
		
		{
			Library glu = new Library("glu");
			glu.incs.add("GL/glu.h");
			glu.deps.add("gl");
			glu.libNames.put(Target.LINUX, "GLU");
			glu.libNames.put(Target.SOLARIS, "GLU");
			glu.libNames.put(Target.WIN, "glu" + Target.getArch());
			libs.put(glu.name, glu);
		}
		
		{
			Library glut = new Library("glut");
			glut.incs.add("GL/glut.h");
			glut.deps.add("glu");
			glut.deps.add("gl");
			glut.libNames.put(Target.LINUX, "glut");
			glut.libNames.put(Target.SOLARIS, "glut");
			glut.libNames.put(Target.WIN, "freeglut");
			libs.put(glut.name, glut);
		}
		
		{
			Library glut = new Library("sdl");
			glut.incs.add("SDL/SDL.h");
			glut.libNames.put(Target.LINUX, "SDL");
			glut.libNames.put(Target.SOLARIS, "SDL");
			glut.libNames.put(Target.WIN, "SDLmain");
			glut.libNames.put(Target.WIN, "SDL");
			libs.put(glut.name, glut);
		}
		
		{
			Library pthread = new Library("pthread");
			pthread.libNames.put(Target.LINUX, "pthread");
			pthread.libNames.put(Target.SOLARIS, "pthread");
			libs.put(pthread.name, pthread);
		}
		
		{
			Library gc = new Library("gc");
			gc.incs.add("gc/gc.h");
			gc.libNames.put(Target.LINUX, "gc");
			gc.libNames.put(Target.SOLARIS, "gc");
			gc.libNames.put(Target.WIN, "gc");
			// FIXME it should be possible to set different dependencies on different platforms
			gc.deps.add("pthread");
			gc.staticByDefault = true;
			libs.put(gc.name, gc);
		}
		
		{
			Library gtk = new Library("gtk");
			gtk.incs.add("gtk/gtk.h");
			gtk.pkgNames.add("gtk+-2.0");
			libs.put(gtk.name, gtk);
		}
		
		{
			Library gtkglarea = new Library("gtkglarea");
			gtkglarea.incs.add("gtkgl/gtkglarea.h");
			gtkglarea.deps.add("gtk");
			gtkglarea.deps.add("gl");
			gtkglarea.pkgNames.add("gtkgl-2.0");
			libs.put(gtkglarea.name, gtkglarea);
		}
		
	}
	
	/**
	 * Add a list of libraries, e.g. if "-ldummy" is needed on the gcc commandline,
	 * this method would add the string "dummy" to the list. Also handles static
	 * linking, e.g. adding "libs/linux/libgc.a" to the list, for example.
	 * @param name The name of the needed library, as said in a Use, for example.
	 * @param dynamicLibraries the string list to add libraries to
	 * @param staticLibraries 
	 */
	public void resolveLibraries(String name, ProjectInfo projInfo) {

		BuildProperties props = projInfo.props;
		List<String> staticLibraries = projInfo.staticLibraries;
		List<String> dynamicLibraries = projInfo.dynamicLibraries;
		
		Library lib = libs.get(name);
		if(lib != null) {
			
			String path = lib.libNames.get(Target.guessHost());
			if(path != null) {
				if(lib.staticByDefault) {
					// FIXME ugly hack. There should be a mechanism to find where
					// is the library instead of just assuming it's in the last element of libPath
					// maybe org.ooc.compiler.SourcePath can be transformed in something more generic?
					String staticLibPath = props.libPath.get(props.libPath.size() - 1)
					+ File.separator + Target.guessHost() + File.separator + "lib" + path + ".a";
					String realPath = staticLibPath.replace(File.separator + File.separator, File.separator);
					addOnce(realPath, staticLibraries);
				} else {
					addOnce(path, dynamicLibraries);
				}
			}
			
			for(String pkgName: lib.pkgNames) {
				if(addOnce(PkgConfigFrontend.getInfo(pkgName), props.pkgInfos) && props.verbose) {
					System.out.println("[LibraryManager] Added library "+name);
				}
			}
			
			for(String dep: lib.deps) {
				resolveLibraries(dep, projInfo);
			}
			
		} else {
			
			if(addOnce(name, dynamicLibraries) && props.verbose) {
				System.out.println("Added library "+name);
			}
			
		}
		
	}
	
	/**
	 * Add the needed Include nodes after this Use node, according to which
	 * library it imports.
	 * @param use The Use to determine which headers to import from.
	 */
	public void resolveIncludes(Use use) {
		
		resolveIncludes(use, use.name);
		
	}
	
	protected void resolveIncludes(Use use, String name) {
		
		Library lib = libs.get(name);
		if(lib != null) {
		
			for(String inc: lib.incs) {
				use.getParent().addAfter(use, new Include(use.location, IncludeType.PATHBOUND, IncludePosition.HEADER, inc));
			}
		
			for(String dep: lib.deps) {
				resolveIncludes(use, dep);
			}
		
		}
		
	}

	protected <T> boolean addOnce(T element, List<T> list) {
		
		if(list.contains(element)) {
			return false;
		}

		list.add(element);
		return true;

	}
	
}
