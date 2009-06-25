package org.ooc;

import java.io.File;
import java.io.StringWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import org.ooc.compiler.ProcessUtils;
import org.ooc.compiler.ReadEnv;

/**
 * Utilities for launching processes
 *  
 * @author Amos Wenger
 */
public class ShellUtils {

	/**
	 * @return the path of an executable, if it can be found. It looks in the PATH
	 * environment variable.
	 */
	public static File findExecutable(String executableName) {
		
		Properties env;
		try {
			env = ReadEnv.getEnvVars();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String pathVar = env.getProperty("PATH");
		if(pathVar == null) {
			pathVar = env.getProperty("Path");
		} if(pathVar == null) {
			pathVar = env.getProperty("path");
		} if(pathVar == null) {
			System.err.println("PATH environment variable not found!");
			return null;
		}
	
		StringTokenizer st = new StringTokenizer(pathVar, File.pathSeparator);
		while(st.hasMoreTokens()) {
			String path = st.nextToken();
			File file = new File(path, executableName);
			if(file.exists()) {
				return file;
			}
		}
		
		return null;
		
	}

	/**
	 * Run a command to get its output
	 * @param command
	 * @return the output of the command specified, once it has exited
	 */
	public static String getOutput(String... command) {
		
		String result = null;
		
		try {
			
			StringWriter writer = new StringWriter();
			Process p = new ProcessBuilder(command).start();
			ProcessUtils.redirectIO(p, writer);
			int exitCode = p.waitFor();
			if(exitCode == 0) {
				result = writer.toString();
			}
			
		} catch (Exception e) {
			/* Exception? Will return null */
			e.printStackTrace();
		}
		
		return result;
		
	}
	
}
