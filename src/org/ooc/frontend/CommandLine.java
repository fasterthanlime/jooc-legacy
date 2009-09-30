package org.ooc.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.ooc.backend.cdirty.CGenerator;
import org.ooc.frontend.compilers.AbstractCompiler;
import org.ooc.frontend.compilers.Clang;
import org.ooc.frontend.compilers.Gcc;
import org.ooc.frontend.compilers.Icc;
import org.ooc.frontend.compilers.Tcc;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.Include.Mode;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.parser.ModuleParser;
import org.ooc.frontend.parser.Parser;
import org.ooc.frontend.pkgconfig.PkgConfigFrontend;
import org.ooc.frontend.pkgconfig.PkgInfo;
import org.ooc.middle.Tinkerer;
import org.ooc.middle.UseDef;
import org.ooc.middle.UseDef.Requirement;
import org.ooc.utils.FileUtils;
import org.ooc.utils.ProcessUtils;
import org.ooc.utils.ShellUtils;
import org.ubi.CompilationFailedError;
import org.ubi.FileLocation;

public class CommandLine {
	
	public static void main(String[] argv) throws InterruptedException, IOException {
		new CommandLine(argv);
	}
	
	protected BuildParams params = new BuildParams();
	List<String> additionals = new ArrayList<String>();
	List<String> compilerArgs = new ArrayList<String>();
	
	public CommandLine(String[] args) throws InterruptedException, IOException {
		
		List<String> modulePaths = new ArrayList<String>();
		List<String> nasms = new ArrayList<String>();
		
		for(String arg: args) {
			if(arg.startsWith("-")) {
        		String option = arg.substring(1);
        		if(option.startsWith("sourcepath")) {
        			
        			String sourcePathOption = arg.substring(arg.indexOf('=') + 1);
        			StringTokenizer tokenizer = new StringTokenizer(sourcePathOption, File.pathSeparator);
        			while(tokenizer.hasMoreTokens()) {
        				params.sourcePath.add(tokenizer.nextToken());
        			}
        			
        		} else if(option.startsWith("outpath")) {
        			
        			params.outPath = new File(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("incpath")) {
        			
        			params.incPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("I")) {
        			
        			params.incPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("libpath")) {
        			
        			params.libPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("editor")) {
        			
        			params.editor = arg.substring(arg.indexOf('=') + 1);
        			
        		} else if(option.equals("c")) {
        			
        			params.link = false;
        			
        		} else if(option.startsWith("L")) {
        			
        			params.libPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("l")) {
        			
        			params.dynamicLibs.add(arg.substring(2));
        			
        		} else if(option.equals("dyngc")) {
        			
        			params.dynGC = true;
        			
        		} else if(option.equals("nogc")) {
        			
        			params.enableGC = false;
        			
        		} else if(option.equals("noclean")) {
        			
        			params.clean = false;
        			
        		} else if(option.equals("shout")) {
        			
        			params.shout = true;
        			
        		} else if(option.equals("timing") || option.equals("t")) {
        			
        			params.timing = true;
        			
        		} else if(option.equals("debug") || option.equals("g")) {
        			
        			params.debug = true;
        			params.clean = false;
        			
        		} else if(option.equals("verbose") || option.equals("v")) {
        			
        			params.verbose = true;
        			
        		} else if(option.equals("veryVerbose") || option.equals("vv")) {
        			
        			params.veryVerbose = true;
        			
        		} else if(option.equals("run") || option.equals("r")) {
        			
        			params.run = true;
        			
        		} else if(option.startsWith("blowup=")) {
        			
        			params.blowup = Integer.parseInt(option.substring("blowup=".length()));
        			
        		} else if(option.equals("V") || option.equals("-version") || option.equals("version")) {
        			
        			CompilerVersion.printVersion();
        			System.exit(0);
        			
        		} else if(option.equals("h") || option.equals("-help") || option.equals("help")) {
        			
        			Help.printHelp();
        			System.exit(0);
        			
        		} else if(option.startsWith("gcc")) {
        			if(option.startsWith("gcc=")) {
        				params.compiler = new Gcc(option.substring(4));
        			} else {
        				params.compiler = new Gcc();
        			}
        		} else if(option.startsWith("icc")) {
        			if(option.startsWith("icc=")) {
        				params.compiler = new Icc(option.substring(4));
        			} else {
        				params.compiler = new Icc();
        			}
        		} else if(option.startsWith("tcc")) {
        			if(option.startsWith("tcc=")) {
        				params.compiler = new Tcc(option.substring(4));
        			} else {
        				params.compiler = new Tcc();
        			}
				} else if(option.startsWith("clang")) {
					if(option.startsWith("clang=")) {
        				params.compiler = new Clang(option.substring(6));
        			} else {
        				params.compiler = new Clang();
        			}
        		} else if(option.equals("help-backends") || option.equals("-help-backends")) {
        			
        			Help.printHelpBackends();
        			System.exit(0);
        			
        		} else if(option.equals("help-gcc") || option.equals("-help-gcc")) {
        			
        			Help.printHelpGcc();
        			System.exit(0);
        			
        		} else if(option.equals("help-make") || option.equals("-help-make")) {
        			
        			Help.printHelpMake();
        			System.exit(0);
        			
        		} else if(option.equals("help-none") || option.equals("-help-none")) {
        			
        			Help.printHelpNone();
        			System.exit(0);
        			
        		} else if(option.equals("slave")) {
        			
        			params.slave = true;

				} else if(option.startsWith("m")) {
					
					String arch = arg.substring(2);
					if (arch.equals("32") || arch.equals("64"))
						params.arch = arg.substring(2);
					else
						System.out.println("Unrecognized architecture: " + arch);
			
        		} else {
        			
        			System.err.println("Unrecognized option: '"+arg+"'");
        			
        		}
        	} else if(arg.startsWith("+")) {
        		compilerArgs.add(arg.substring(1));
        	} else {
        			String lowerArg = arg.toLowerCase();
					if(lowerArg.endsWith(".s")) {
        				nasms.add(arg);
        			} else if(lowerArg.endsWith(".o") || lowerArg.endsWith(".c") || lowerArg.endsWith(".cpp")) {
            			additionals.add(arg);
            		} else {
            			if(!lowerArg.endsWith(".ooc")) {
            				modulePaths.add(arg+".ooc");
            			} else {
            				modulePaths.add(arg);
            			}
            		}
        	}
		}
		
		if(modulePaths.isEmpty()) {
			System.err.println("ooc: no files.");
			return;
		}
		
		if(params.compiler == null) params.compiler = new Gcc();
		
		if(!nasms.isEmpty()) {
			compileNasms(nasms, additionals);
		}
		
		if(params.sourcePath.isEmpty()) params.sourcePath.add(".");
		params.sourcePath.add(params.sdkLocation.getPath());
	
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		do {
			ModuleParser.clearCache();
			int successCount = 0;
			for(String modulePath: modulePaths) {
				try {
					int code = parse(modulePath);
					if(code == 0) successCount++;
				} catch(CompilationFailedError err) {
					System.err.println(err);
					fail();
					if(params.editor.length() > 0) {
						launchEditor(params.editor, err);
					}
				}
				if(params.clean) FileUtils.deleteRecursive(params.outPath);
			}
			
			if(modulePaths.size() > 1) {
				System.out.println(modulePaths.size()+" compiled ("+successCount
						+" success, "+(modulePaths.size() - successCount)+" failed)");
			}
			
			if(params.slave) {
				System.out.println(".-------------( ready )-------------.\n");
				reader.readLine();
			} else {
				if(successCount < modulePaths.size()) {
					System.exit(1);
				}
			}
			
		} while(params.slave);
		
	}

	private void ok() {
		if(params.shout) {
			if(Target.guessHost() == Target.LINUX) {
				System.out.println("\033[1;32m[ OK ]\033[m");
			} else {
				System.out.println("[ OK ]");
			}
		}
	}

	private void fail() {
		if(params.shout) {
			if(Target.guessHost() == Target.LINUX) {
				System.out.println("\033[1;31m[FAIL]\033[m");
			} else {
				System.out.println("[FAIL]");
			}
		}
	}
	
	private void launchEditor(final String editor, final CompilationFailedError err) {
		
		if(err.getLocation() == null) return;
		
		//Thread thread = new Thread() {
			//@Override
			//public void run() {
				try {
					ProcessBuilder builder = new ProcessBuilder();
					FileLocation location = err.getLocation();
					String absolutePath = new File(location.getFileName()).getAbsolutePath();
					if(editor.equals("geany")) {
						builder.command(editor, absolutePath+":"+location.getLineNumber()+":"+(location.getLinePos() - 1));
					} else if(editor.equals("mate")) {
						builder.command(editor, absolutePath, "-l", String.valueOf(location.getLineNumber()));
					} else {
						builder.command(editor, absolutePath);
					}
					ProcessUtils.redirectIO(builder.start());
				} catch (IOException e) {
					e.printStackTrace();
				}
			//}
		//};
		//thread.setDaemon(true);
		//thread.start();
		
	}

	private void compileNasms(List<String> nasms, Collection<String> list) throws IOException, InterruptedException {
		
		boolean has = false;
		if(nasms.isEmpty()) return;
		if(params.verbose) {
			System.out.println("Should compile nasms "+nasms);
		}
		
		List<String> reallyNasms = new ArrayList<String>();
		for(String nasm: nasms) {
			if(nasm.endsWith(".s")) {
				reallyNasms.add(nasm);
				has = true;
			}
		}
		
		if(has) {
			List<String> command = new ArrayList<String>();
			command.add(findExec("nasm").getPath());
			command.add("-f");
			command.add("elf");
			command.addAll(reallyNasms);
			
			ProcessBuilder builder = new ProcessBuilder(command);
			Process process = builder.start();
			ProcessUtils.redirectIO(process);
			int code = process.waitFor();
			if(code != 0) {
				System.err.println("nasm failed, aborting compilation process");
				System.exit(code);
			}
			
			for(String nasm: nasms) {
				if(nasm.endsWith(".s")) {
					list.add(nasm.substring(0, nasm.length() - 1) + "o");
				} else {
					list.add(nasm);
				}
			}
		} else {
			list.addAll(nasms);
		}
		
	}

	protected int parse(String modulePath) throws InterruptedException, IOException {
		
		params.outPath.mkdirs();
		long tt1 = System.nanoTime();
		Module module = new Parser(params).parse(modulePath);
		module.setMain(true);
		long tt2 = System.nanoTime();
		
		ArrayList<Module> list = new ArrayList<Module>();
		collectModules(module, list);
		tinker(list);
		
		long tt3 = System.nanoTime();
		output(module, new HashSet<Module>());
		long tt4 = System.nanoTime();
		int code = compile(module);
		long tt5 = System.nanoTime();

		if(params.timing) {
			System.out.printf("parse: %.2f ms\ttinker: %.2f ms\toutput: %.2f ms\tcc: %.2f ms\tTOTAL %.2f ms\n",
					Float.valueOf((tt2 - tt1) / 1000000.0f),
					Float.valueOf((tt3 - tt2) / 1000000.0f),
					Float.valueOf((tt4 - tt3) / 1000000.0f),
					Float.valueOf((tt5 - tt4) / 1000000.0f),
					Float.valueOf((tt5 - tt1) / 1000000.0f));
		}
		
		if(code == 0) {
			if(params.shout) ok();
			if(params.run) {
				ProcessBuilder builder = new ProcessBuilder();
				builder.command("./"+module.getSimpleName());
				Process process = builder.start();
				ProcessUtils.redirectIO(process);
				int exitCode = process.waitFor();
				if(exitCode != 0) {
					System.out.println("Unerwarteter Programmabbruch. Return code: "+exitCode+". Please don't cry :(");
				}
			}
		} else if(params.shout) fail();
		return code;
		
	}
	
	protected void output(Module module, Set<Module> done) throws IOException {
		done.add(module);
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				output(imp.getModule(), done);
			}
		}
		new CGenerator(params.outPath, module).generate(params);
	}

	protected void collectModules(Module module, List<Module> list) throws IOException {
		list.add(module);
		for(Import imp: module.getImports()) {
			if(!list.contains(imp.getModule())) {
				collectModules(imp.getModule(), list);
			}
		}
	}
	
	protected void tinker(List<Module> list) throws IOException {
		Tinkerer tink = new Tinkerer();
		tink.process(list, params);
	}

	protected int compile(Module module) throws Error,
			IOException, InterruptedException {
		
		params.compiler.reset();
		
		copyLocalHeaders(module, new HashSet<Module>());
		
		if(params.debug) params.compiler.setDebugEnabled();		
		params.compiler.addIncludePath(new File(params.distLocation, "libs/headers/").getPath());
		params.compiler.addIncludePath(params.outPath.getPath());
		addDeps(params.compiler, module, new HashSet<Module>());
		for(String dynamicLib: params.dynamicLibs) {
			params.compiler.addDynamicLibrary(dynamicLib);
		}
		for(String additional: additionals) {
			params.compiler.addObjectFile(additional);
		}
		for(String compilerArg: compilerArgs) {
			params.compiler.addObjectFile(compilerArg);
		}
		
		if(params.link) {
			params.compiler.setOutputPath(module.getSimpleName());
			Collection<String> libs = getFlagsFromUse(module);
			for(String lib: libs) params.compiler.addObjectFile(lib);
			
			if(params.enableGC) {
				params.compiler.addDynamicLibrary("pthread");
				if(params.dynGC) {
					params.compiler.addDynamicLibrary("gc");
				} else {
					params.compiler.addObjectFile(new File(params.distLocation, "libs/"
							+ Target.guessHost().toString(params.arch.equals("") ? Target.getArch() : params.arch) + "/libgc.a").getPath());
				}
			}
		} else {
			params.compiler.setCompileOnly();
		}
		
		if(params.verbose) params.compiler.printCommandLine();
		
		int code = params.compiler.launch();
		if(code != 0) {
			System.err.println("C compiler failed, aborting compilation process");
		}
		return code;
		
	}

	private void copyLocalHeaders(Module module, HashSet<Module> done) {
		
		if(done.contains(module)) return;
		done.add(module);
		for(Include inc: module.getIncludes()) {
			if(inc.getMode() == Mode.LOCAL) {
				try {
					File file = params.sourcePath.getFile(module.getPath()).getParentFile();
					FileUtils.copy(new File(file, inc.getPath() + ".h"),
						new File(params.outPath, inc.getPath() + ".h"));
				} catch(Exception e) { e.printStackTrace(); }
			}
		}
		
		for(Import imp: module.getImports()) {
			copyLocalHeaders(imp.getModule(), done);
		}
		
	}

	protected Collection<String> getFlagsFromUse(Module module) throws IOException, InterruptedException {

		Set<String> list = new HashSet<String>();
		Set<Module> done = new HashSet<Module>();
		getFlagsFromUse(module, list, done, new HashSet<UseDef>());
		return list;
		
	}

	protected void getFlagsFromUse(Module module, Set<String> flagsDone, Set<Module> modulesDone, Set<UseDef> usesDone) throws IOException, InterruptedException {

		if(modulesDone.contains(module)) return;
		modulesDone.add(module);
		
		for(Use use: module.getUses()) {
			UseDef useDef = use.getUseDef();
			getFlagsFromUse(useDef, flagsDone, usesDone);
		}
		
		for(Import imp: module.getImports()) {
			getFlagsFromUse(imp.getModule(), flagsDone, modulesDone, usesDone);
		}
		
	}

	private void getFlagsFromUse(UseDef useDef, Set<String> flagsDone,
			Set<UseDef> usesDone) throws IOException, InterruptedException {
		
		if(usesDone.contains(useDef)) return;
		usesDone.add(useDef);
		compileNasms(useDef.getLibs(), flagsDone);
		for(String pkg: useDef.getPkgs()) {
			PkgInfo info = PkgConfigFrontend.getInfo(pkg);
			for(String cflag: info.cflags) {
				if(!flagsDone.contains(cflag)) {
					flagsDone.add(cflag);
				}
			}
			for(String library: info.libraries) {
				 // FIXME lazy
				String lpath = "-l"+library;
				if(!flagsDone.contains(lpath)) {
					flagsDone.add(lpath);
				}
			}
		}
		for(String includePath: useDef.getIncludePaths()) {
			 // FIXME lazy too.
			String ipath = "-I"+includePath;
			if(!flagsDone.contains(ipath)) {
				flagsDone.add(ipath);
			}
		}
		
		for(String libPath: useDef.getLibPaths()) {
			 // FIXME lazy too.
			String lpath = "-L"+libPath;
			if(!flagsDone.contains(lpath)) {
				flagsDone.add(lpath);
			}
		}
		
		for(Requirement req: useDef.getRequirements()) {
			getFlagsFromUse(req.getUseDef(), flagsDone, usesDone);
		}
		
	}

	protected File findExec(String name) throws Error {
		
		File execFile = ShellUtils.findExecutable(name);
		if(execFile == null) {
			execFile = ShellUtils.findExecutable(name+".exe");
		}
		if(execFile == null) {
			throw new Error(name+" not found :/");
		}
		return execFile;
		
	}

	protected void addDeps(AbstractCompiler compiler, Module module, Set<Module> done) {
		
		done.add(module);
		compiler.addObjectFile(new File(params.outPath, module.getPath(".c")).getPath());
		
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				addDeps(compiler, imp.getModule(), done);
			}
		}
		
	}

}
