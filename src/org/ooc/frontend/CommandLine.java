package org.ooc.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.ooc.backend.cdirty.CGenerator;
import org.ooc.frontend.compilers.Clang;
import org.ooc.frontend.compilers.Gcc;
import org.ooc.frontend.compilers.Icc;
import org.ooc.frontend.compilers.Tcc;
import org.ooc.frontend.drivers.CombineDriver;
import org.ooc.frontend.drivers.Driver;
import org.ooc.frontend.drivers.SequenceDriver;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.parser.ModuleParser;
import org.ooc.frontend.parser.Parser;
import org.ooc.middle.Tinkerer;
import org.ooc.utils.FileUtils;
import org.ooc.utils.ProcessUtils;
import org.ubi.CompilationFailedError;
import org.ubi.FileLocation;

public class CommandLine {
	
	public static void main(String[] argv) throws InterruptedException, IOException {
		new CommandLine(argv);
	}
	
	private BuildParams params = new BuildParams();
	private Driver driver = new CombineDriver(params);
	
	class Pair {
		String in;
		String out;
		
		public Pair(String in) {
			this.in = in;
			this.out = null;
		}
	}
	
	public CommandLine(String[] args) throws InterruptedException, IOException {
		
		List<Pair> modulePaths = new ArrayList<Pair>();
		List<String> nasms = new ArrayList<String>();
		params.compiler = new Gcc();
		
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
        			params.clean = false;
        			
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
        			
        		} else if(option.startsWith("D")) {
        			
        			params.defines.add(arg.substring(2));
        			
        		} else if(option.startsWith("U")) {
        			
        			params.defines.remove(arg.substring(2));
        			
        		} else if(option.startsWith("l")) {
        			
        			params.dynamicLibs.add(arg.substring(2));
        			
        		} else if(option.equals("dyngc")) {
        			
        			System.out.println("Deprecated option -dyngc, you should use -gc=dynamic instead.");
        			params.dynGC = true;
        			params.defineSymbol(BuildParams.GC_DEFINE);
        			
        		} else if(option.equals("nogc")) {
        			
        			System.out.println("Deprecated option -nogc, you should use -gc=off instead.");
        			params.enableGC = false;
        			params.undefineSymbol(BuildParams.GC_DEFINE);
        			
        		} else if(option.startsWith("gc=")) {
        			
        			String subOption = option.substring(3);
        			if(subOption.equals("off")) {
        				params.enableGC = false;
        				params.undefineSymbol(BuildParams.GC_DEFINE);
        			} else if(subOption.equals("dynamic")) {
        				params.enableGC = true;
        				params.defineSymbol(BuildParams.GC_DEFINE);
        				params.dynGC = true;
        			} else if(subOption.equals("static")) {
        				params.enableGC = true;
        				params.defineSymbol(BuildParams.GC_DEFINE);
        				params.dynGC = false;
        			} else {
        				System.out.println("Unrecognized option "+option
        						+", valid values are gc=off, gc=dynamic, gc=static");
        			}
        			
        		} else if(option.equals("noclean")) {
        			
        			params.clean = false;
        			
        		} else if(option.equals("nolines")) {
        			
        			params.lineDirectives = false;
        			
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
        			
        		} else if(option.startsWith("o=")) {
        			
        			if(modulePaths.isEmpty()) {
        				System.out.println("Using '-o' option before any .ooc file, ignoring..." +
        						"\n(you should do something like ooc file.ooc -o myexecutable");
        			} else {
        				modulePaths.get(modulePaths.size() - 1).out = option.substring(2);
        			}
        			
        		} else if(option.startsWith("driver=")) {
        			
        			String driverName = option.substring("driver=".length());
        			if(driverName.equals("combine")) {
        				driver = new CombineDriver(params);
        			} else if(driverName.equals("sequence")) {
        				driver = new SequenceDriver(params);
        			} else {
        				System.out.println("Unknown driver '"+driverName+"'");
        			}
        			
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
        			params.dynGC = true;
				} else if(option.startsWith("clang")) {
					if(option.startsWith("clang=")) {
        				params.compiler = new Clang(option.substring(6));
        			} else {
        				params.compiler = new Clang();
        			}
        		} else if(option.equals("onlygen")) {
					params.compiler = null;
					params.clean = false;
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
			
				} else if(option.startsWith("archs=")) {
					
					params.fatArchitectures = arg.substring("archs=".length() + 1).split(",");
					
				} else if(option.startsWith("osxtarget=")) {
					
					params.osxSDKAndDeploymentTarget = arg.substring("osxtarget=".length() + 1);
					
				} else {
        			
        			System.err.println("Unrecognized option: '"+arg+"'");
        			
        		}
        	} else if(arg.startsWith("+")) {
        		driver.compilerArgs.add(arg.substring(1));
        	} else {
        			String lowerArg = arg.toLowerCase();
					if(lowerArg.endsWith(".s")) {
        				nasms.add(arg);
        			} else if(lowerArg.endsWith(".o") || lowerArg.endsWith(".c") || lowerArg.endsWith(".cpp")) {
        				driver.additionals.add(arg);
            		} else {
            			if(!lowerArg.endsWith(".ooc")) {
            				modulePaths.add(new Pair(arg+".ooc"));
            			} else {
            				modulePaths.add(new Pair(arg));
            			}
            		}
        	}
		}
		
		if(modulePaths.isEmpty()) {
			System.err.println("ooc: no files.");
			return;
		}
		
		if(!nasms.isEmpty()) {
			driver.compileNasms(nasms, driver.additionals);
		}
		
		if(params.sourcePath.isEmpty()) params.sourcePath.add(".");
		params.sourcePath.add(params.sdkLocation.getPath());
	
		int errorCode = 0;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		do {
			ModuleParser.clearCache();
			int successCount = 0;
			for(Pair modulePath: modulePaths) {
				try {
					int code = parse(modulePath);
					if(code == 0) {
						successCount++;
					} else {
						errorCode = 2; // C compiler failure.
					}
				} catch(CompilationFailedError err) {
					if(errorCode == 0) errorCode = 1; // ooc failure
					System.err.println(err);
					fail();
					if(params.editor.length() > 0) {
						System.out.println("Press [Enter] to launch "+params.editor);
						reader.readLine();
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
					System.exit(errorCode);
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
		
		Thread thread = new Thread() {
			@Override
			public void run() {
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
			}
		};
		thread.setDaemon(true);
		thread.start();
		try {
			// allow time for the program startup
			Thread.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	protected int parse(Pair modulePath) throws InterruptedException, IOException {
		
		params.outPath.mkdirs();
		long tt1 = System.nanoTime();
		Module module = new Parser(params).parse(modulePath.in);
		module.setMain(true);
		long tt2 = System.nanoTime();
		
		ArrayList<Module> list = new ArrayList<Module>();
		collectModules(module, list);
		tinker(list);
		
		long tt3 = System.nanoTime();
		output(module, new HashSet<Module>());
		long tt4 = System.nanoTime();
		int code = 0;
		if(params.compiler != null) {
			code = driver.compile(module, modulePath.out == null ? module.getSimpleName() : modulePath.out);
		}
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

}
