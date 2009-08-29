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
	
	@Override
	public void reset() {
		command.clear();
		command.add(executablePath);
	}
	
}
