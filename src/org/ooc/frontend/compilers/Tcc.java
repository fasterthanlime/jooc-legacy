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
	
	@Override
	public void reset() {
		command.clear();
		command.add(executablePath);
	}
	
}
