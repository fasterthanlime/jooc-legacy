package org.ooc.frontend.compilers;

/**
 * Intel C++ Compiler
 * 
 * @author Amos Wenger
 */
public class Icc extends Gcc {

	public Icc() {
		super("icc");
	}
	
	public Icc(String executableName) {
		super(executableName);
	}
	
	@Override
	public void reset() {
		command.clear();
		command.add(executablePath);
	}
	
}
