package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.BuildParams;
import org.ooc.frontend.model.Module;

public interface Hobgoblin {

	public boolean process(Module module, BuildParams params) throws IOException;
	
}
