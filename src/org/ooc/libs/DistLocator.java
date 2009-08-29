package org.ooc.libs;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import org.ooc.outputting.FileUtils;

public class DistLocator {

	public static File locate() {
		
		try {
		
			File location;
			
			Map<String, String> env = System.getenv();
			Object envDist = env.get("OOC_DIST");
			if(envDist != null) {
				return new File(envDist.toString());
			}
			
			location = tryUnderscore(env);
			if(location != null) return location;
			
			location = tryClassPath();
			if(location != null) return location;
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	/**
	 * Assume we're launched as GCJ-compiled executable, and
	 * try to get our path from the "_" environment variable, which seems to
	 * be set by bash under Gentoo and by MSYS 1.0.11/whatever under MinGW/WinXP
	 * @throws IOException 
	 */
	protected static File tryUnderscore(Map<String, String> env) throws IOException {
		
		Object underscore = env.get("_");
		if(underscore != null) {
			File file = new File(underscore.toString().trim());
			if(file.getPath().endsWith("ooc") || file.getPath().endsWith("ooc.exe")) {
				String canonicalPath = file.getCanonicalPath();
				return new File(canonicalPath).getParentFile().getParentFile();
			}
		}
		return null;
		
	}

	/** 
	 * Assume we're launched with java -jar bin/ooc.jar or
	 * java -classpath path/to/ooc/build/classes/ org.ooc.frontend.CommandLine
	 * and try to find ourselves in the classpath.
	 */
	protected static File tryClassPath() {
		
		String classPath = System.getProperty("java.class.path");
		
		StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			String base = "";
			for(int i = 0; i < 8; i++) { // brute force. works for the .class and the .jar methods
				base += "../";
				try {
					File distribLocation = new File(token, base).getCanonicalFile();
					File idFile = FileUtils.resolveRedundancies(new File(distribLocation, "sdk/ooc_sdk_id"));
					if(idFile.exists()) {
						return FileUtils.resolveRedundancies(distribLocation);
					}
				} catch(IOException e) {}
			}
		}
		
		return null;
	}
	
}
