package org.ooc.frontend.compilers;

/**
 * Clang (C-language, LLVM-based) Compiler
 */
public class Clang extends Gcc 
{
	public Clang() 
	{
		super("clang");
	}
	
	@Override
	public void reset() 
	{
		command.clear();
		command.add(executablePath);
	}
}
