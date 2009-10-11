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
import org.ooc.frontend.drivers.Driver;
import org.ooc.frontend.drivers.SequenceDriver;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
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
	
	private Driver driver = new SequenceDriver();
	
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
        				driver.params.sourcePath.add(tokenizer.nextToken());
        			}
        			
        		} else if(option.startsWith("outpath")) {
        			
        			driver.params.outPath = new File(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("incpath")) {
        			
        			driver.params.incPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("I")) {
        			
        			driver.params.incPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("libpath")) {
        			
        			driver.params.libPath.add(arg.substring(arg.indexOf('=') + 1));
        			
        		} else if(option.startsWith("editor")) {
        			
        			driver.params.editor = arg.substring(arg.indexOf('=') + 1);
        			
        		} else if(option.equals("c")) {
        			
        			driver.params.link = false;
        			
        		} else if(option.startsWith("L")) {
        			
        			driver.params.libPath.add(arg.substring(2));
        			
        		} else if(option.startsWith("l")) {
        			
        			driver.params.dynamicLibs.add(arg.substring(2));
        			
        		} else if(option.equals("dyngc")) {
        			
        			driver.params.dynGC = true;
        			
        		} else if(option.equals("nogc")) {
        			
        			driver.params.enableGC = false;
        			
        		} else if(option.equals("noclean")) {
        			
        			driver.params.clean = false;
        			
        		} else if(option.equals("shout")) {
        			
        			driver.params.shout = true;
        			
        		} else if(option.equals("timing") || option.equals("t")) {
        			
        			driver.params.timing = true;
        			
        		} else if(option.equals("debug") || option.equals("g")) {
        			
        			driver.params.debug = true;
        			driver.params.clean = false;
        			
        		} else if(option.equals("verbose") || option.equals("v")) {
        			
        			driver.params.verbose = true;
        			
        		} else if(option.equals("veryVerbose") || option.equals("vv")) {
        			
        			driver.params.veryVerbose = true;
        			
        		} else if(option.equals("run") || option.equals("r")) {
        			
        			driver.params.run = true;
        			
        		} else if(option.startsWith("blowup=")) {
        			
        			driver.params.blowup = Integer.parseInt(option.substring("blowup=".length()));
        			
        		} else if(option.equals("V") || option.equals("-version") || option.equals("version")) {
        			
        			CompilerVersion.printVersion();
        			System.exit(0);
        			
        		} else if(option.equals("h") || option.equals("-help") || option.equals("help")) {
        			
        			Help.printHelp();
        			System.exit(0);
        			
        		} else if(option.startsWith("gcc")) {
        			if(option.startsWith("gcc=")) {
        				driver.params.compiler = new Gcc(option.substring(4));
        			} else {
        				driver.params.compiler = new Gcc();
        			}
        		} else if(option.startsWith("icc")) {
        			if(option.startsWith("icc=")) {
        				driver.params.compiler = new Icc(option.substring(4));
        			} else {
        				driver.params.compiler = new Icc();
        			}
        		} else if(option.startsWith("tcc")) {
        			if(option.startsWith("tcc=")) {
        				driver.params.compiler = new Tcc(option.substring(4));
        			} else {
        				driver.params.compiler = new Tcc();
        			}
				} else if(option.startsWith("clang")) {
					if(option.startsWith("clang=")) {
        				driver.params.compiler = new Clang(option.substring(6));
        			} else {
        				driver.params.compiler = new Clang();
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
        			
        			driver.params.slave = true;

				} else if(option.startsWith("m")) {
					
					String arch = arg.substring(2);
					if (arch.equals("32") || arch.equals("64"))
						driver.params.arch = arg.substring(2);
					else
						System.out.println("Unrecognized architecture: " + arch);
			
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
		
		if(driver.params.compiler == null) driver.params.compiler = new Gcc();
		
		if(!nasms.isEmpty()) {
			driver.compileNasms(nasms, driver.additionals);
		}
		
		if(driver.params.sourcePath.isEmpty()) driver.params.sourcePath.add(".");
		driver.params.sourcePath.add(driver.params.sdkLocation.getPath());
	
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
					if(driver.params.editor.length() > 0) {
						launchEditor(driver.params.editor, err);
					}
				}
				if(driver.params.clean) FileUtils.deleteRecursive(driver.params.outPath);
			}
			
			if(modulePaths.size() > 1) {
				System.out.println(modulePaths.size()+" compiled ("+successCount
						+" success, "+(modulePaths.size() - successCount)+" failed)");
			}
			
			if(driver.params.slave) {
				System.out.println(".-------------( ready )-------------.\n");
				reader.readLine();
			} else {
				if(successCount < modulePaths.size()) {
					System.exit(1);
				}
			}
			
		} while(driver.params.slave);
		
	}

	private void ok() {
		if(driver.params.shout) {
			if(Target.guessHost() == Target.LINUX) {
				System.out.println("\033[1;32m[ OK ]\033[m");
			} else {
				System.out.println("[ OK ]");
			}
		}
	}

	private void fail() {
		if(driver.params.shout) {
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

	protected int parse(String modulePath) throws InterruptedException, IOException {
		
		driver.params.outPath.mkdirs();
		long tt1 = System.nanoTime();
		Module module = new Parser(driver.params).parse(modulePath);
		module.setMain(true);
		long tt2 = System.nanoTime();
		
		ArrayList<Module> list = new ArrayList<Module>();
		collectModules(module, list);
		tinker(list);
		
		long tt3 = System.nanoTime();
		output(module, new HashSet<Module>());
		long tt4 = System.nanoTime();
		int code = driver.compile(module);
		long tt5 = System.nanoTime();

		if(driver.params.timing) {
			System.out.printf("parse: %.2f ms\ttinker: %.2f ms\toutput: %.2f ms\tcc: %.2f ms\tTOTAL %.2f ms\n",
					Float.valueOf((tt2 - tt1) / 1000000.0f),
					Float.valueOf((tt3 - tt2) / 1000000.0f),
					Float.valueOf((tt4 - tt3) / 1000000.0f),
					Float.valueOf((tt5 - tt4) / 1000000.0f),
					Float.valueOf((tt5 - tt1) / 1000000.0f));
		}
		
		if(code == 0) {
			if(driver.params.shout) ok();
			if(driver.params.run) {
				ProcessBuilder builder = new ProcessBuilder();
				builder.command("./"+module.getSimpleName());
				Process process = builder.start();
				ProcessUtils.redirectIO(process);
				int exitCode = process.waitFor();
				if(exitCode != 0) {
					System.out.println("Unerwarteter Programmabbruch. Return code: "+exitCode+". Please don't cry :(");
				}
			}
		} else if(driver.params.shout) fail();
		return code;
		
	}
	
	protected void output(Module module, Set<Module> done) throws IOException {
		done.add(module);
		for(Import imp: module.getImports()) {
			if(!done.contains(imp.getModule())) {
				output(imp.getModule(), done);
			}
		}
		new CGenerator(driver.params.outPath, module).generate(driver.params);
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
		tink.process(list, driver.params);
	}

}
