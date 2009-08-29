package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.*;

public class CoverDeclWriter {

	public static void write(CoverDecl cover, CGenerator cgen) throws IOException {
		cgen.current = cgen.hw;

		// addons only add functions to an already imported cover, so
		// we don't need to struct it again, it would confuse the C compiler
		if(!cover.isAddon() && !cover.isExtern() && cover.getFromType() == null) {
			cgen.current.app("struct _").app(cover.getName()).app(' ').openBlock();
			for(VariableDecl decl: cover.getVariables()) {
				cgen.current.nl();
				decl.accept(cgen);
				cgen.current.app(';');
			}
			cgen.current.closeBlock().app(';').nl();
		}
		
		for(FunctionDecl decl: cover.getFunctions()) {
			decl.accept(cgen);
			cgen.current.nl();
		}
	}

	public static  void writeTypedef(CoverDecl cover, CGenerator cgen) throws IOException {
		
		if(!cover.isAddon() && !cover.isExtern()) {
			Type fromType = cover.getFromType();
			if(fromType == null) {
				cgen.current.nl().app("typedef struct _").app(cover.getName()).app(' ').app(cover.getName()).app(';');
			} else {
				cgen.current.nl().app("typedef ");
				TypeWriter.writeSpaced(fromType.getGroundType(), cgen);
				cgen.current.app(cover.getName()).app(';');
			}
		}
	}
	
}
