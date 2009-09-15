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
		File execFile = ShellUtils.findExecutable(executableName);
		if(execFile == null) {
			ShellUtils.findExecutable(executableName + ".exe");
			if(execFile == null) {
				ShellUtils.findExecutable(executableName, true);
			}
		}
		executablePath = execFile.getName();
		reset();
	}
	
	@Override
	public int launch() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		Process process = builder.start();
		ProcessUtils.redirectIO(process);
		return process.waitFor();
	}
	
	@Override
	public void printCommandLine() {
		StringBuilder commandLine = new StringBuilder();
		for(String arg: command) {
			commandLine.append(arg);
			commandLine.append(' ');
		}
		System.out.println(commandLine.toString());
	}
	
	@Override
	public void reset() {
		command.clear();
		command.add(executablePath);
	}

}
