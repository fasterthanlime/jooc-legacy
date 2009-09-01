package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.Type;

public class TypeWriter {

	public static void write(Type type, CGenerator cgen) throws IOException {
		if(type.getName().equals("Func")) {
			writeFuncPointer(((FunctionDecl) type.getRef()), "", cgen);
			return;
		}
		
		if(type.getRef() instanceof GenericType) {
			cgen.current.append("Octet *");
			return;
		}
		
		if(type.isConst()) cgen.current.app("const ");
		cgen.current.app(type.getName());
		
		if(!type.isFlat()) {
			cgen.current.app(' ');
		}
		
		if(type.getRef() == null) {
			throw new Error("Unresolved type '"+type.getName()+"' !!");
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
		return cgen.current.app("void (*");
	}
	
}
