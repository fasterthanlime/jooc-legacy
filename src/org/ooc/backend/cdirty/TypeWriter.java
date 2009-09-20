package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.middle.OocCompilationError;

public class TypeWriter {

	public static void write(Type type, CGenerator cgen) throws IOException {
		write(type, cgen, true);		
	}
	
	public static void write(Type type, CGenerator cgen, boolean doPrefix) throws IOException {
		if(type.getRef() == null) {
			throw new OocCompilationError(type, cgen.module, "Unresolved type '"+type.getName()+"' !!");
		}
		
		if(type.getName().equals("Func")) {
			writeFuncPointer(((FunctionDecl) type.getRef()), "", cgen);
			return;
		}
		
		if(type.getRef() instanceof TypeParam) {
			cgen.current.append("Octet *");
			return;
		}
		
		if(type.isConst()) cgen.current.app("const ");
		if(doPrefix && type.getRef() instanceof TypeDecl) {
			cgen.current.app(((TypeDecl) type.getRef()).getUnderName());
		} else {
			cgen.current.app(type.getName());
		}
		
		if(!type.isFlat()) {
			cgen.current.app(' ');
		}
		
		writeFinale(type, cgen);
	}
	
	public static  void writeFinale(Type type, CGenerator cgen) throws IOException {
		if(type.getRef() instanceof ClassDecl) {
			cgen.current.app('*');
		}
		
		int level = type.getPointerLevel() + type.getReferenceLevel();
		for(int i = 0; i < level; i++) {
			if(type.isArray()) {
				if(i == 0 && type.getArraySize() != null) {
					cgen.current.app('[');
					type.getArraySize().accept(cgen);
					cgen.current.app(']');
				} else {
					cgen.current.app("[]");
				}
			} else {
				cgen.current.app('*');
			}
		}
	}
	
	public static void writeSpaced(Type type, CGenerator cgen) throws IOException {
		writeSpaced(type, cgen, true);
	}
	
	public static void writeSpaced(Type type, CGenerator cgen, boolean doPrefix) throws IOException {
		write(type, cgen, doPrefix);
		if(type.isFlat()) cgen.current.app(' ');
	}

	public static void writeFuncPointer(FunctionDecl decl, String name, CGenerator cgen) throws IOException {
		writeFuncPointerStart(decl, cgen);
		cgen.current.app(name);
		writeFuncPointerEnd(decl, cgen);		
	}

	public static AwesomeWriter writeFuncPointerEnd(FunctionDecl decl, CGenerator cgen)
			throws IOException {
		return cgen.current.app(")()");
	}

	public static AwesomeWriter writeFuncPointerStart(FunctionDecl decl, CGenerator cgen)
			throws IOException {
		decl.getReturnType().accept(cgen);
		return cgen.current.app(" (*");
	}
	
}
