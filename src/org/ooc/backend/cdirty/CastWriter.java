package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.VariableAccess;

public class CastWriter {

	public static void write(Cast cast, CGenerator cgen) throws IOException {
		if(cast.getExpression().getType().getRef() instanceof GenericType) {
			VariableAccess access = (VariableAccess) cast.getExpression();
			cgen.current.app("*((");
			cast.getType().accept(cgen);
			cgen.current.app("*)");
			AccessWriter.writeVariable(access, false, cgen);
			cgen.current.app(')');
			return;
		}
		
		cgen.current.app("((");
		cast.getType().accept(cgen);
		cgen.current.app(") ");
		cast.getExpression().accept(cgen);
		cgen.current.app(")");
	}
	
}
