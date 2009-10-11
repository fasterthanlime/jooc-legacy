package org.ooc.frontend.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.ooc.frontend.Target;
import org.ooc.frontend.compilers.AbstractCompiler;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;

public class SequenceDriver extends Driver {

	int finalCode;
	int count;
	
	@Override
	public int compile(Module module) throws Error, IOException, InterruptedException {
		
		copyLocalHeaders(module, params, new HashSet<Module>());
		
		HashSet<Module> toCompile = collectDeps(module, new HashSet<Module>());
		
		final ArrayList<String> oPaths = new ArrayList<String> ();
		final long tt0 = System.nanoTime(); 
		final Iterator<Module> iterator = toCompile.iterator();
		// since we have several worker threads, we have to us an AtomicInteger
		final AtomicInteger count = new AtomicInteger(toCompile.size());		
		
		finalCode = 0;
	
		Runnable runnable = new Runnable() {
			public void run() {
				
				AbstractCompiler compiler = params.compiler.clone();
				
				while(iterator.hasNext()) {
					
					Module currentModule = null;
					
					synchronized(iterator) {
						currentModule = iterator.next();
						iterator.remove();
					}
				
					initCompiler(compiler);
					compiler.setCompileOnly();
					
					String path = new File(params.outPath, currentModule.getPath("")).getPath();
					String oPath = path + ".o";
					String cPath = path + ".c";
					oPaths.add(oPath);
					
					if(new File(cPath).lastModified() > new File(oPath).lastModified()) {
					
						compiler.addObjectFile(cPath);
						compiler.setOutputPath(oPath);
						
						if(params.verbose) System.out.print(compiler.getCommandLine());
						
						long tt1 = System.nanoTime();
						int code = -1;
						try {
							code = compiler.launch();
						} catch (Exception e) {
							e.printStackTrace();
						}
						long tt2 = System.nanoTime();
						if(params.verbose) System.out.println("  (" + ((tt2 - tt1) / 1000000)+"ms)");
							
						if(code != 0) {
							System.err.println("C compiler failed, aborting compilation process");
							finalCode = code;
						}
						
					} else {
						
						if(params.verbose)
							System.out.println("Skipping "+cPath+", just the same.");
						
					}
					
					synchronized(iterator) {
						count.decrementAndGet();
					}

				}		
			}
		};
		
		for(int i = 0; i < 4; i++) {
			new Thread(runnable).start();
		}
		
		while(count.get() > 0) {
			Thread.sleep(100L);
		}
		
		if(params.link) {
			
			initCompiler(params.compiler);
			
			for(String oPath: oPaths) {
				params.compiler.addObjectFile(oPath);
			}
			
			for(String dynamicLib: params.dynamicLibs) {
				params.compiler.addDynamicLibrary(dynamicLib);
			}
			for(String additional: additionals) {
				params.compiler.addObjectFile(additional);
			}
			
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
		}
		
		if(params.verbose) System.out.println(params.compiler.getCommandLine());

		long tt1 = System.nanoTime();
		int code = params.compiler.launch();
		long tt2 = System.nanoTime();
		if(params.verbose) System.out.println("  (linking " + ((tt2 - tt1) / 1000000)+"ms)");
		
		if(params.verbose) {
			System.out.println("(total " + ((System.nanoTime() - tt0) / 1000000)+"ms)");
		}
		
		if(code != 0) {
			System.err.println("C compiler failed, aborting compilation process");
			return code;
		}
		
		return 0;
		
	}

	void initCompiler(AbstractCompiler compiler) {
		compiler.reset();
		
		if(params.debug) compiler.setDebugEnabled();
		compiler.addIncludePath(new File(params.distLocation, "libs/headers/").getPath());
		compiler.addIncludePath(params.outPath.getPath());
		
		for(String compilerArg: compilerArgs) {
			compiler.addObjectFile(compilerArg);
		}
	}

	private HashSet<Module> collectDeps(Module module, HashSet<Module> toCompile) {
		
		toCompile.add(module);
		
		for(Import import1: module.getImports()) {
			if(toCompile.contains(import1.getModule())) continue;
			collectDeps(import1.getModule(), toCompile);
		}
		
		return toCompile;
		
	}
	
}
