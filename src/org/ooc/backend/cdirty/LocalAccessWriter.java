package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.VariableAccess;

public class LocalAccessWriter {

	public static void write(VariableAccess variableAccess, boolean doTypeParams, CGenerator cgen) throws IOException {
		write(variableAccess, doTypeParams, cgen, 0);
	}
	
	public static void write(VariableAccess variableAccess, boolean doTypeParams, CGenerator cgen, int refOffset) throws IOException {
		
		if(variableAccess.getRef() instanceof TypeDecl && !(variableAccess.getRef() instanceof TypeParam)) {
			cgen.current.app(variableAccess.getName()).app("_class()");
			return;
		}
		
		int refLevel = variableAccess.getRef().getType().getReferenceLevel();
		
		if(doTypeParams) {
			if(variableAccess.getType().isGeneric()) {
				refLevel++;
			}
		}
		
		refLevel += refOffset;
		
		if(refLevel > 0) {
			cgen.current.app('(');
			for(int i = 0; i < refLevel; i++) {
				cgen.current.app('*');
			}
		}
		cgen.current.app(variableAccess.getRef().getExternName(variableAccess));
		if(refLevel > 0) {
			cgen.current.app(')');
		}
		
	}
	
}
