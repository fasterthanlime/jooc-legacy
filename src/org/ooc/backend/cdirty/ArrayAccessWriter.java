package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ArrayAccess;

public class ArrayAccessWriter {

	public static void write(ArrayAccess arrayAccess, CGenerator cgen) throws IOException {
		arrayAccess.getVariable().accept(cgen);
		cgen.current.app('[');
		arrayAccess.getIndex().accept(cgen);
		cgen.current.app(']');
	}
	
}
