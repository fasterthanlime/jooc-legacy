package org.ooc.frontend.compilers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.ShellUtils;
import org.ooc.compiler.ProcessUtils;

public abstract class BaseCompiler implements AbstractCompiler {
	
	protected List<String> command = new ArrayList<String>();
	protected String executablePath;
	
	public BaseCompiler(String executableName) {
		executablePath = ShellUtils.findExecutable(executableName).getName();
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
