package org.ooc.frontend.compilers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.utils.ProcessUtils;
import org.ooc.utils.ShellUtils;

public abstract class BaseCompiler implements AbstractCompiler {
	
	protected List<String> command = new ArrayList<String>();
	protected String executablePath;
	
	@SuppressWarnings("null")
	public BaseCompiler(String executableName) {
		File execFile = new File(executableName);
		if(!execFile.exists()) {
			execFile = ShellUtils.findExecutable(executableName);
			if(execFile == null) {
				execFile = ShellUtils.findExecutable(executableName + ".exe");
				if(execFile == null) {
					ShellUtils.findExecutable(executableName, true);
				}
			}
		}
		executablePath = execFile.getAbsolutePath();
		reset();
	}
	
	public int launch() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		Process process = builder.start();
		ProcessUtils.redirectIO(process);
		return process.waitFor();
	}
	
	public String getCommandLine() {
		StringBuilder commandLine = new StringBuilder();
		for(String arg: command) {
			commandLine.append(arg);
			commandLine.append(' ');
		}
		return commandLine.toString();
	}
	
	public void reset() {
		command.clear();
		command.add(executablePath);
	}
	
	@Override
	public abstract BaseCompiler clone();

}
