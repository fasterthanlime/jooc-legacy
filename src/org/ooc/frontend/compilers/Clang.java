package org.ooc.frontend.compilers;

/**
 * Clang (C-language, LLVM-based) Compiler
 */
public class Clang extends Gcc {
	
	public Clang()  {
		super("clang");
	}
	
	public Clang(String executableName) {
		super(executableName);
	}

	@Override
	public void reset() {
		command.clear();
		command.add(executablePath);
	}
	
	@Override
	public Clang clone() {
		return new Clang();
	}
	
}
