package org.ooc.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;

/**
 * Somehow like the 'classpath' in Java. E.g. holds where to find ooc
 * modules, and well, find them when asked kindly to.
 * 
 * @author Amos Wenger
 */
public class SourcePath {
	
	private HashMap<String, File> paths;
	
	/**
	 * Default constructor
	 */
	public SourcePath() {
		
		paths = new HashMap<String, File> ();
		
	}
	
	/**
	 * Add an element to the classpath
	 * @param path
	 */
	public void add(String path) {
		
		File file = new File(path);
		if(!file.exists()) {
			System.err.println("Classpath element cannot be found: "+file.getPath());
			return;
		} else if(!file.isDirectory()) {
			System.err.println("Classpath element is not a directory: "+file.getPath());
			return;
		}
		
		String absolutePath = file.getAbsolutePath();
		if(!paths.containsKey(absolutePath)) {
			paths.put(absolutePath, file);
		}
		
	}
	
	/**
	 * Remove an element from the sourcepath
	 * @param path
	 */
	public void remove(String path) {
		
		File file = new File(path);
		if(!file.exists()) {
			System.err.println("Classpath element cannot be found: "+file.getPath());
			return;
		} else if(!file.isDirectory()) {
			System.err.println("Classpath element is not a directory: "+file.getPath());
			return;
		} else if(!paths.containsKey(file.getAbsolutePath())) {
			System.err.println("Trying to remove a previously unexisting element: "+file.getPath()+", ignoring.");
			return;
		}
		paths.remove(file.getAbsolutePath());
		
	}
	
	/**
	 * Remove all elements from the sourcepath
	 */
	public void clear() {
		
		paths.clear();
		
	}
	
	/**
	 * @return all files in the source path
	 */
	public Collection<File> getPaths() {
		
		return paths.values();
		
	}
	
	/**
	 * Get the path element in which SourceInfo was found.
	 * @param info
	 * @return
	 * @throws FileNotFoundException
	 */
	public File getPathElement(SourceInfo info) throws FileNotFoundException {
		
		for(File sourcePathElement: paths.values()) {
			File candidate = new File(sourcePathElement, info.getPath());
			if(candidate.exists()) {
				return sourcePathElement;
			}
		}
		
		throw new FileNotFoundException("Class not found : '"+info.fullName+"', sourcePath = "+paths);

	}
	
	/**
	 * Find the module described by the specified SourceInfo in the
	 * source path and return a file to its .ooc source
	 * @param info
	 * @return
	 * @throws FileNotFoundException
	 */
	public File getFile(SourceInfo info) throws FileNotFoundException {
		
		String path = info.getPath();
		
		for(File sourcePathElement: paths.values()) {
			File candidate = new File(sourcePathElement, path);
			if(candidate.exists()) {
				info.sourceElementPath = sourcePathElement.getPath();
				return candidate;
			}
		}
		
		throw new FileNotFoundException("Class not found : '"+info.fullName+"', sourcePath = "+paths);

	}

	/**
	 * @return true if the source path is empty
	 */
	public boolean isEmpty() {

		return paths.isEmpty();
		
	}
	
	@Override
	public String toString() {
		
		return paths.toString();
		
	}

}
