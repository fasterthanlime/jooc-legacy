package org.ooc.frontend.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.Include.Mode;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.pkgconfig.PkgConfigFrontend;
import org.ooc.frontend.pkgconfig.PkgInfo;
import org.ooc.middle.UseDef;
import org.ooc.middle.UseDef.Requirement;
import org.ooc.utils.FileUtils;
import org.ooc.utils.ProcessUtils;
import org.ooc.utils.ShellUtils;

/**
 * Drives the compilation process, e.g. chooses in which order
 * files are compiled, optionally checks for timestamps and stuff.
 * Great fun.
 * 
 * @author Amos Wenger
 */
public abstract class Driver {

	public BuildParams params;
	public List<String> additionals = new ArrayList<String>();
	public List<String> compilerArgs = new ArrayList<String>();
	
	public Driver(BuildParams params) {
		super();
		this.params = params;
	}

	public abstract int compile(Module module, String outName) throws Error,
			IOException, InterruptedException;
	
	protected void copyLocalHeaders(Module module, BuildParams params, HashSet<Module> done) {
		
		if(done.contains(module)) return;
		done.add(module);
		for(Include inc: module.getIncludes()) {
			if(inc.getMode() == Mode.LOCAL) {
				try {
					File file = params.sourcePath.getFile(module.getPrefixLessPath()).getParentFile();
					
					File target = new File(params.outPath, inc.getPath() + ".h");
					target.getParentFile().mkdirs();
					FileUtils.copy(new File(file, inc.getPath() + ".h"),
						target);
				} catch(Exception e) { e.printStackTrace(); }
			}
		}
		
		for(Import imp: module.getAllImports()) {
			copyLocalHeaders(imp.getModule(), params, done);
		}
		
	}
	
	protected void addDeps(Module module, Set<Module> toCompile, Set<String> done) {
		
		toCompile.add(module);
		done.add(module.getPath());
		
		params.compiler.addObjectFile(new File(params.outPath, module.getPath(".c")).getPath());
		
		for(Import imp: module.getAllImports()) {
			if(!done.contains(imp.getModule().getPath())) {
				addDeps(imp.getModule(), toCompile, done);
			}
		}
		
	}
	
	protected Collection<String> getFlagsFromUse(Module module, boolean doLinking) throws IOException, InterruptedException {

		Set<String> list = new HashSet<String>();
		Set<Module> done = new HashSet<Module>();
		getFlagsFromUse(module, list, done, new HashSet<UseDef>(), doLinking);
		return list;
		
	}

	protected void getFlagsFromUse(Module module, Set<String> flagsDone,
			Set<Module> modulesDone, Set<UseDef> usesDone, boolean doLinking) throws IOException, InterruptedException {

		if(modulesDone.contains(module)) return;
		modulesDone.add(module);
		
		for(Use use: module.getUses()) {
			UseDef useDef = use.getUseDef();
			getFlagsFromUse(useDef, flagsDone, usesDone, doLinking);
		}
		
		for(Import imp: module.getAllImports()) {
			getFlagsFromUse(imp.getModule(), flagsDone, modulesDone, usesDone, doLinking);
		}
		
	}

	private void getFlagsFromUse(UseDef useDef, Set<String> flagsDone,
			Set<UseDef> usesDone, boolean doLinking) throws IOException, InterruptedException {
		
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
			if(doLinking) {
				for(String library: info.libraries) {
					// FIXME lazy
					String lpath = "-l"+library;
					if(!flagsDone.contains(lpath)) {
						flagsDone.add(lpath);
					}
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
			getFlagsFromUse(req.getUseDef(), flagsDone, usesDone, doLinking);
		}
		
	}
	
	public void compileNasms(List<String> nasms, Collection<String> list) throws IOException, InterruptedException {
		
		boolean has = false;
		if(nasms.isEmpty()) return;
		
		List<String> reallyNasms = new ArrayList<String>();
		for(String nasm: nasms) {
			if(nasm.endsWith(".s")) {
				reallyNasms.add(nasm);
				has = true;
			}
		}
		
		if(has) {
			if(params.verbose) {
				System.out.println("Should compile nasms "+reallyNasms);
			}
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
	
}
