package org.ooc.frontend.compilers;

/**
 * Gnu Compilers Collection 
 * 
 * @author Amos Wenger
 */
public class Gcc extends BaseCompiler {

	public Gcc() {
		super("gcc");
	}
	
	public Gcc(String executableName) {
		super(executableName);
	}

	public void addDynamicLibrary(String library) {
		command.add("-l"+library);
	}

	public void addIncludePath(String path) {
		command.add("-I"+path);
	}

	public void addLibraryPath(String path) {
		command.add("-L"+path);
	}

	public void addObjectFile(String file) {
		command.add(file);
	}

	public void addOption(String option) {
		command.add(option);
	}

	public void setOutputPath(String path) {
		command.add("-o");
		command.add(path);
	}

	public void setCompileOnly() {
		command.add("-c");
	}

	public void setDebugEnabled() {
		command.add("-g");
	}
	
	@Override
	public void reset() {
		super.reset();
		command.add("-std=c99");
		command.add("-Wall");
        command.add("-O2");
	}

	public boolean supportsDeclInFor() {
		return true;
	}

	public boolean supportsVLAs() {
		return true;
	}
	
	@Override
	public Gcc clone() {
		return new Gcc();
	}

	public void defineSymbol(String symbolName) {
		command.add("-D" + symbolName);
	}

	public void undefineSymbol(String symbolName) {
		command.add("-U" + symbolName);		
	}

	public void setFatArchitectures(String[] archs) {
		for (String arch: archs) {
			command.add("-arch");
			command.add(arch);
		}
	}
	
	public void setOSXSDKAndDeploymentTarget(String version) {
		command.add("-isysroot");
		command.add("/Developer/SDKs/MacOSX" + version + ".sdk");
		command.add("-mmacosx-version-min=" + version);
	}
	
}
