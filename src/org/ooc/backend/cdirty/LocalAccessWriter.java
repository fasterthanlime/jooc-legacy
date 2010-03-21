package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;

public class LocalAccessWriter {

	public static void write(VariableAccess variableAccess, boolean doTypeParams, CGenerator cgen) throws IOException {
		write(variableAccess, doTypeParams, cgen, 0);
	}
	
	public static void write(VariableAccess variableAccess, boolean doTypeParams, CGenerator cgen, int refOffset) throws IOException {
		
		if(variableAccess.getRef() instanceof TypeDecl && !(variableAccess.getRef() instanceof TypeParam)) {
			TypeDecl ref = (TypeDecl) variableAccess.getRef();
			if(ref instanceof CoverDecl && ((CoverDecl)ref).isAddon()) {
				ref = ((CoverDecl)ref).getBase();
			}
			cgen.current.app(ref.getUnderName()).app("_class()");
			return;
		}

		// duplicated code with MemberAccessWriter: modularize!
		int refLevel = variableAccess.getRef().getType().getReferenceLevel();
		refLevel += refOffset;
		
		if(refLevel > 0) {
			cgen.current.app('(');
			for(int i = 0; i < refLevel; i++) {
				cgen.current.app('*');
			}
		}
		if(variableAccess.getRef() instanceof VariableDecl) {
			VariableDecl ref = (VariableDecl)variableAccess.getRef();
			cgen.current.app(ref.getFullName());
		} else if(variableAccess.getRef() instanceof FunctionDecl) {
			// closure, man!
			cgen.current.app(((FunctionDecl)variableAccess.getRef()).getFullName());
		} else {
			cgen.current.app(variableAccess.getRef().getExternName());
		}
		if(refLevel > 0) cgen.current.app(')');
		
	}
	
}
