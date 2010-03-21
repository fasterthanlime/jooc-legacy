package org.ooc.frontend.drivers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.ooc.frontend.BuildParams;
import org.ooc.frontend.Target;
import org.ooc.frontend.model.Module;

public class CombineDriver extends Driver {

	public CombineDriver(BuildParams params) {
		super(params);
	}

	@Override
	public int compile(Module module, String outName) throws Error, IOException, InterruptedException {
		
		params.compiler.reset();
		
		copyLocalHeaders(module, params, new HashSet<Module>());
		
		if(params.debug) params.compiler.setDebugEnabled();		
		params.compiler.addIncludePath(new File(params.distLocation, "libs/headers/").getPath());
		params.compiler.addIncludePath(params.outPath.getPath());
		addDeps(module, new HashSet<Module>(), new HashSet<String>());
		for(String define: params.defines) {
			params.compiler.defineSymbol(define);
		}
		for(String dynamicLib: params.dynamicLibs) {
			params.compiler.addDynamicLibrary(dynamicLib);
		}
		for(String additional: params.additionals) {
			params.compiler.addObjectFile(additional);
		}
		for(String compilerArg: params.compilerArgs) {
			params.compiler.addObjectFile(compilerArg);
		}
		for (File incPath: params.incPath.getPaths()) {
			params.compiler.addIncludePath(incPath.getAbsolutePath());
		}
		for (File libPath: params.libPath.getPaths()) {
			params.compiler.addLibraryPath(libPath.getAbsolutePath());
		}
		
		if (params.fatArchitectures != null) {
			params.compiler.setFatArchitectures(params.fatArchitectures);
		}
		if (params.osxSDKAndDeploymentTarget != null) {
			params.compiler.setOSXSDKAndDeploymentTarget(params.osxSDKAndDeploymentTarget);
		}
		
		Collection<String> libs = getFlagsFromUse(module, params.link);
		for(String lib: libs) params.compiler.addObjectFile(lib);
		
		if(params.link) {
			params.compiler.setOutputPath(outName);
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
		
		if(params.verbose) System.out.println(params.compiler.getCommandLine());
		
		int code = params.compiler.launch();
		if(code != 0) {
			System.err.println("C compiler failed, aborting compilation process");
		}
		return code;
		
	}
	
}
