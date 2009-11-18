package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.VariableAccess;

public class CastWriter {

	public static void write(Cast cast, CGenerator cgen) throws IOException {
		if(cast.getType().equals(cast.getExpression().getType())) {
			cast.getExpression().accept(cgen);
			return;
		}
		
		if(cast.getExpression().getType().getRef() instanceof TypeParam) {
			Expression expr = cast.getExpression();
			if(expr instanceof VariableAccess) {
				VariableAccess access = (VariableAccess) expr;
				cgen.current.app("*((");
				cast.getType().accept(cgen);
				cgen.current.app("*)(");
				AccessWriter.write(access, false, cgen);
				cgen.current.app("))");
				return;
			} else if(expr instanceof ArrayAccess) {
				ArrayAccess access = (ArrayAccess) expr;
				cgen.current.app("*((");
				cast.getType().accept(cgen);
				cgen.current.app("*) (");
				access.getVariable().accept(cgen);
				cgen.current.app(" + ");
				// FIXME that's.. probably not quite right =)
				assert(access.getIndices().size() == 1);
				access.getIndices().getFirst().accept(cgen);
				cgen.current.app(" * ").app(cast.getType().getMangledName());
				cgen.current.app("_class()->size))");
				return;
			}
		}
		
		cgen.current.app("((");
		cast.getType().accept(cgen);
		cgen.current.app(") (");
		cast.getExpression().accept(cgen);
		cgen.current.app("))");
	}
	
}