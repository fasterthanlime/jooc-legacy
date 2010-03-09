package org.ooc.middle;

import java.io.IOException;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.BuildParams;

public interface Hobgoblin {

	public boolean process(Module module, BuildParams params) throws IOException;
	
}
