package org.ooc.frontend.compilers;

/**
 * TinyCC - originally by Fabrice Bellard 
 * 
 * @author Amos Wenger
 */
public class Tcc extends Gcc {

	public Tcc() {
		super("tcc");
	}
	
	public Tcc(String executableName) {
		super(executableName);
	}

	@Override
	public void reset() {
		command.clear();
		command.add(executablePath);
	}
	
}
