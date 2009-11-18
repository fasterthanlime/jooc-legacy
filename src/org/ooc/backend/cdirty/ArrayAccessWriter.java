package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Expression;

public class ArrayAccessWriter {

	public static void write(ArrayAccess arrayAccess, CGenerator cgen) throws IOException {
		arrayAccess.getVariable().accept(cgen);
		for(Expression index: arrayAccess.getIndices()) {
			cgen.current.app('[');
			index.accept(cgen);
			cgen.current.app(']');
		}
	}
	
}
