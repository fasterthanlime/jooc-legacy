package org.ooc.frontend;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Somehow like the 'classpath' in Java. E.g. holds where to find ooc
 * modules, and well, find them when asked kindly to.
 * 
 * @author Amos Wenger
 */
public class PathList {
	
	protected HashMap<String, File> paths;
	
	/**
	 * Default constructor
	 */
	public PathList() {
		
		paths = new HashMap<String, File> ();
		
	}
	
	/**
	 * Add an element to the classpath
	 * @param path
	 */
	public void add(String path) {
		
		File file = new File(path);
		if(!file.exists()) {
			System.err.println("Classpath element cannot be found: "+path);
			return;
		} else if(!file.isDirectory()) {
			System.err.println("Classpath element is not a directory: "+path);
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
	 * Return a list of all files found in a directory in the whole sourcepath
	 * @param path
	 * @return
	 */
	public Collection<String> getRelativePaths(String path) {
		
		List<String> files = new ArrayList<String>();
		
		for(File element: paths.values()) {
			File candidate = new File(element, path);
			if(candidate.exists()) addChildren(path, files, candidate);
		}
		
		return files;
		
	}

	private void addChildren(String basePath, List<String> list, File parent) {
		
		File[] children = parent.listFiles();
		for(File child: children) {
			if(child.isFile()) {
				list.add(basePath + '/' + child.getName());
			} else if(child.isDirectory()) {
				addChildren(basePath + '/' + child.getName(), list, child);
			}
		}
		
	}
	
	/**
	 * Find the file in the source path and return a File object associated to it
	 */
	public File getFile(String path) {
		
		File element = getElement(path);
		return element == null ? null : new File(element, path);

	}
	
	/**
	 * Find the file in the source path and return the element of the path list
	 * it has been found in.
	 */
	public File getElement(String path) {
		
		for(File element: paths.values()) {
			File candidate = new File(element, path);
			if(candidate.exists()) {
				return element;
			}
		}
		
		return null;

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
