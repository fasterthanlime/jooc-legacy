package org.ooc.backends;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ooc.ShellUtils;
import org.ooc.compiler.BuildProperties;
import org.ooc.compiler.ProcessUtils;
import org.ooc.compiler.libraries.Target;
import org.ooc.compiler.pkgconfig.PkgInfo;
import org.ooc.errors.SourceContext;
import org.ooc.outputting.CachedFileOutputter;
import org.ooc.outputting.FileOutputter;
import org.ooc.outputting.FileUtils;

/**
 * The official GCC (Gnu Compiler Collection) backend. It is also
 * the default when no particular backend is specified on the command line.
 * 
 * Backend options:
 * <ul>
 * 	<li>-clean=[yes,no] : If yes, delete C source and headers files, and binary
 * object files. In fact, leave just the executables.</li>
 * 	<li>-verbose=[yes,no] : If yes, prints each command (mostly 'gcc') that is
 * launched by the backend (useful to check correctness)</li>
 * </ul>
 * 
 * @author Amos Wenger
 */
class GccBackend extends Backend {

	private String gccPath;
	private List<String> cFlags;
	private boolean clean;
	private boolean verbose;
	private boolean shout;
	private FileOutputter fileOutputter;
	
	private class CommandFailedException extends Exception {

		private static final long serialVersionUID = -3792947243264725228L;
		public final int returnCode;
		
		public CommandFailedException(String commandName, int returnCode) {
			super(commandName+" failed with return code "+returnCode);
			this.returnCode = returnCode;
		}
		
	}
	
	protected GccBackend(String parameters) {
		
		super(parameters);
		
		cFlags = new ArrayList<String> ();
		cFlags.add("-std=c99");
		//cFlags.add("-Wall");
		//cFlags.add("-pedantic");
		
		// defaults
		clean = true;
		verbose = false;
		shout = false;
		
		StringTokenizer tokenizer = new StringTokenizer(parameters, ",");
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(token.startsWith("-clean=")) {
				clean = parseYesNo(token, "-clean=", clean);
			} else if(token.equals("-s")) {
				shout = true;
			} else if(token.startsWith("-shout=")) {
				shout = parseYesNo(token, "-shout=", shout);
			} else if(token.equals("-v")) {
				verbose = true;
			} else if(token.startsWith("-verbose=")) {
				verbose = parseYesNo(token, "-verbose=", verbose);
			} else {
				cFlags.add(token);
				if(token.equals("-g")) {
					clean = false;
				}
			}
		}
			
		determineGccPath();
		
		fileOutputter = new CachedFileOutputter();
		
	}

	/**
	 * 
	 * @param token the token that starts with optionFullName and ends with "yes" or "no"
	 * @param optionFullName example "-option="
	 * @param defaultValue the value that should be returned if the token doesn't end with "yes" or "no"
	 * @return the parsed value, or defaultValue
	 */
	private boolean parseYesNo(String token, String optionFullName, boolean defaultValue) {
		
		String yesNo = token.substring(optionFullName.length()); 
		if(yesNo.equals("yes")) {
			return true;
		} else if(yesNo.equals("no")) {
			return false;
		} else {
			System.err.println("invalid option parameter: '"+token+"'. Can be '"+optionFullName
					+"yes' or '"+optionFullName+"no'");
			return defaultValue;
		}
		
	}

	/**
	 * Tries to find gcc. First try the CC environment variable.
	 * If not set, tries to find 'gcc' and 'gcc.exe' in the PATH.
	 */
	private void determineGccPath() throws Error {
		
		Map<String, String> env = System.getenv();
		
		if(env.containsKey("CC")) {
			
			gccPath = env.get("CC");
			
		} else {
			
			File file = ShellUtils.findExecutable("gcc");
			if(file == null) {
				file = ShellUtils.findExecutable("gcc.exe");
			}
			if(file != null) {
				gccPath = file.getPath();
			}
			
		}
		
		if(gccPath == null || gccPath.isEmpty()) { // Still empty ?
			throw new Error("gcc not found in PATH nor specified as an option, can't compile !");
		}
		
	}
	
	/**
	 * Compile all executables of this ProjectInfo with gcc.
	 * Here is an overview of the steps of the GCC backend:
	 * <ul>
	 * 	<li>Write .ooc files to target directory with FileOutputter.</li>
	 *  <li>Compile all modules (sources that don't have a main()) .ooc files
	 *  to object (.o) files</li>
	 *  <li>Compile all executables, linking only the needed modules.</li>
	 *  <li>If the "clean" option is enabled, removes all C source (.c),
	 *  C headers (.h) and object (.o) files produced during step 2.</li>
	 * </ul>
	 */
	@Override
	public int compile(ProjectInfo info, BuildProperties props) throws IOException, InterruptedException {

		// General parameters override backend-specific ones
		if(clean && !props.clean) {
			clean = false;
		}
		if(!verbose && props.verbose) {
			verbose = true;
		}
		
		List<String> args = new ArrayList<String>();
		List<String> toClean = new ArrayList<String>();
		List<String> toMove = new ArrayList<String>();
		
		try {
			 
			/* Phase 0: write all source */
			for(SourceContext context: info.executables.values()) {
				fileOutputter.output(props, info, context);
			}

			/* Phase 1: compile all modules */
			for(String module: info.modules.keySet()) {
				args.clear();
				addFlags(args, props);
				
				args.add("-c");
				String cPath = info.getOutPath(module, ".c");
				args.add(cPath);
				toClean.add(cPath);
				String hPath = info.getOutPath(module, ".h");
				toClean.add(hPath);
				
				addIncludePaths(info, props, args);
				
				args.add("-o");
				String oPath = info.getOutPath(module, ".o");
				args.add(oPath);
				toClean.add(oPath);
				
				launchGCC(args);
			}

			/* Phase 2: compile all executables */
			for(String executableName: info.executables.keySet()) {
				SourceContext executable = info.executables.get(executableName);
				
				args.clear();
				addFlags(args, props);
				
				String cPath = info.getOutPath(executableName, ".c");
				String hPath = info.getOutPath(executableName, ".h");
				args.add(cPath);
				toClean.add(cPath);
				toClean.add(hPath);
				
				addDependenciesRecursive(info, args, executable);
				addIncludePaths(info, props, args);
				addLibraryPaths(info, props, args);
				
				for(String dynamicLib: info.dynamicLibraries) {
					args.add("-l");
					args.add(dynamicLib);
				}
				
				for(PkgInfo pkg: props.pkgInfos) {
					for(String dynamicLib: pkg.libraries) {
						args.add("-l");
						args.add(dynamicLib);
					}
				}
				
				args.add("-o");
				// Should it be executableName instead? I don't like my executables buried in package folders.
				String execPath = info.getRelativePath(executable.source.getInfo().simpleName, "");
				args.add(execPath);
				toMove.add(execPath);
				
				for(String staticLib: info.staticLibraries) {
					args.add(staticLib);
				}
				
				launchGCC(args);
			}

			/* Phase 3: clean if asked to */
			if(clean) {
				for(String path: toClean) {
					new File(path).delete();
				}
			}
			if(props.outPath.equals(BuildProperties.DEFAULT_OUTPATH)) {
				for(String path: toMove) {
					File file = new File(path);
					file.renameTo(new File(".", file.getName()));
				}
				if(clean) {
					FileUtils.deleteRecursive(new File(props.outPath));
				}
			}
			
		} catch (CommandFailedException e) {
			
			if(shout) {
				if(Target.guessHost() == Target.LINUX) {
					System.out.println("\033[1;31m[FAIL]\033[m");
				} else {
					System.out.println("[FAIL]");
				}
			}
			return 1;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		if(shout) {
			if(Target.guessHost() == Target.LINUX) {
				System.out.println("\033[1;32m[ OK ]\033[m");
			} else {
				System.out.println("[ OK ]");
			}
		}
		return 0;
		
	}

	private void addDependenciesRecursive(ProjectInfo info, List<String> args, SourceContext source) {

		for(SourceContext dependency: source.getDependencies()) {
			String path = info.getOutPath(dependency.source.getInfo().fullName, ".o");
			if(!args.contains(path)) {
				args.add(path);
				addDependenciesRecursive(info, args, dependency);
			}
		}
		
	}

	private void addFlags(List<String> args, BuildProperties props) {
		
		args.add(gccPath);
		args.addAll(cFlags);
		for(PkgInfo pkg: props.pkgInfos) {
			for(String cflag: pkg.cflags) {
				args.add(cflag);
			}
		}
		
	}

	private void addLibraryPaths(ProjectInfo info, BuildProperties props,
			List<String> args) {
		
		for(String path: props.libPath) {
			args.add("-L");
			args.add(path);
		}
		
	}

	private void addIncludePaths(ProjectInfo info, BuildProperties props,
			List<String> args) {
		
		for(String path: props.incPath) {
			args.add("-I");
			args.add(path);
		}
		
	}
	
	private void launchGCC(List<String> args) throws CommandFailedException, IOException, InterruptedException {
		
		StringBuilder commandLine = new StringBuilder();
		for(String arg: args) {
			commandLine.append(arg);
			commandLine.append(' ');
		}
		
		if(verbose) {
			System.out.println(commandLine.toString());
		}
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File("./"));
		builder.command(args);
		Process process = builder.start();
		
		ProcessUtils.redirectIO(process);
		
		int returnCode = process.waitFor();
		if(returnCode != 0) {
			throw new CommandFailedException("gcc", returnCode);
		}
		
	}

}
