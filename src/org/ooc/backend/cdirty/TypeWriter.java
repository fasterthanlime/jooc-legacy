package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeParam;

public class TypeWriter {

	public static void write(Type type, CGenerator cgen) throws IOException {
		if(type.getName().equals("Func")) {
			writeFuncPointer((FuncType) type, "", cgen);
			return;
		}
		
		if(type.getRef() instanceof TypeParam) {
			cgen.current.append("Pointer");
			return;
		}
		
		cgen.current.app(type.getName());
		if(!type.isFlat()) {
			cgen.current.app(' ');
		}
		
		if(type.getRef() == null) {
			throw new Error("Unresolved Type "+type.getName()+" !!");
		}
		
		writeStars(type, cgen);
	}
	
	public static  void writeStars(Type type, CGenerator cgen) throws IOException {
		if(type.getRef() instanceof ClassDecl) {
			cgen.current.app('*');
		}
		
		int level = type.getPointerLevel() + type.getReferenceLevel();
		for(int i = 0; i < level; i++) {
			if(type.isArray()) cgen.current.app("[]");
			else cgen.current.app('*');
		}
	}
	
	public static void writeSpaced(Type type, CGenerator cgen) throws IOException {
		type.accept(cgen);
		if(type.isFlat()) cgen.current.app(' ');
	}

	public static void writeFuncPointer(FuncType type, String name, CGenerator cgen) throws IOException {
		writeFuncPointerStart(type, cgen);
		cgen.current.app(name);
		writeFuncPointerEnd(type, cgen);		
	}

	public static AwesomeWriter writeFuncPointerEnd(FuncType type, CGenerator cgen)
			throws IOException {
		return cgen.current.app(")()");
	}

	public static AwesomeWriter writeFuncPointerStart(FuncType type, CGenerator cgen)
			throws IOException {
		return cgen.current.app("void (*");
	}
	
}
