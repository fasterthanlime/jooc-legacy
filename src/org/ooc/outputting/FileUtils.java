package org.ooc.outputting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A collection of file utilities that should have been in the
 * Java SDK.
 * 
 * @author Amos Wenger
 */
public class FileUtils {

	/**
	 * Recursively deletes a file/folder and all its subcontent.
	 * In other words, "rm -rf"
	 * @param file
	 */
	public static void deleteRecursive(File file) {
	
		List<File> exploreQueue = new ArrayList<File>();
		List<File> exploreQueue2 = new ArrayList<File>();
		List<File> deleteQueue = new ArrayList<File>();
		exploreQueue.add(file);
		
		while(!exploreQueue.isEmpty()) {
			deleteQueue.addAll(0, exploreQueue);
			for(File toExplore: exploreQueue) {
				if(toExplore.isDirectory()) {
					for(File child: toExplore.listFiles()) {
						exploreQueue2.add(0, child);
					}
				}
			}
			exploreQueue.clear();
			exploreQueue.addAll(exploreQueue2);
			exploreQueue2.clear();
		}
		
		for(File toDelete: deleteQueue) {
			toDelete.delete();
		}
		
	}

	/**
	 * Copy a source file to a specified destination path.
	 * @param sourceFile the source
	 * @param destFile the destination (will be created if doesn't exist, will
	 * be overwritten if it exists)
	 * @throws IOException we never know...
	 */
	public static void copy(File sourceFile, File destFile) throws IOException {
		
		if(!destFile.exists()) {
			destFile.createNewFile();
		}
	
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally {
			if(source != null) {
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
		
	}
	
	/**
	 * Read a file contents and return it as a string
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String read(File file) throws IOException {
	
		StringBuilder builder = new StringBuilder((int) file.length());
		FileReader reader = new FileReader(file);
		int n;
		char[] cbuf = new char[4096];
		while((n = reader.read(cbuf)) != -1) {
			builder.append(cbuf, 0, n);
		}
		return builder.toString();
		
	}
	
	/**
	 * Write a string to a file
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void write(File file, String data) throws IOException {
	
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(data);
		writer.close();
	
	}

	/**
	 * Resolve redundancies, ie. ".." and "."
	 * @param dst
	 * @return
	 */
	public static File resolveRedundancies(File dst) {

		String path = dst.getPath();
		List<String> elems = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(path, File.separator);
		boolean startsWithSeparator = path.startsWith(File.separator);
		while(st.hasMoreTokens()) {
			String elem = st.nextToken();
			if(elem.equals("..")) {
				if(!elems.isEmpty()) {
					elems.remove(elems.size() - 1);
				} else {
					elems.add(elem);
				}
			} else if(elem.equals(".")) {
				// do nothing
			} else {
				elems.add(elem);
			}
		}
		StringBuffer buffer = new StringBuffer(path.length());
		if(startsWithSeparator) {
			buffer.append(File.separator);
		}
		int size = elems.size();
		int count = 0;
		for(String elem: elems) {
			buffer.append(elem);
			if(++count < size) {
				buffer.append(File.separator);
			}
		}
		return new File(buffer.toString());
		
	}

	public static File relativize(File toRelativize, File reference) throws IOException {
		
		String toRelPath = toRelativize.getCanonicalPath();
		String refPath = reference.getCanonicalPath();
		
		if(toRelPath.length() > refPath.length()) {
			String diff = toRelPath.substring(refPath.length());
			while(diff.startsWith(File.separator)) diff = diff.substring(1);
			return new File(diff);
		}
		//FIXME too tired to get it right
		return toRelativize;
		
	}
	
}
