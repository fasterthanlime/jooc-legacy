package org.ooc.backend;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.BuildParams;

public abstract class Generator {

	public final Module module;

	public Generator(File outPath, Module module) {
		this.module = module;
	}
	
	public abstract void generate(BuildParams params) throws IOException;
	
}
