package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;

public class VariableDeclWriter {

	public static void write(VariableDecl variableDecl, CGenerator cgen) throws IOException {

		if(variableDecl.isExtern()) return;
		
		// FIXME add const checking from the ooc side of things. Disabled C's
		// const keyword because it causes problems with class initializations
		//if(variableDecl.isConst()) cgen.current.app("const ");
		
		Type type = variableDecl.getType();
		if(type instanceof FuncType) {
			
			FuncType funcType = (FuncType) variableDecl.getType();
			FunctionDecl funcDecl = funcType.getDecl();
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				TypeWriter.writeSpaced(funcDecl.getReturnType(), cgen);
				cgen.current.app("(*").app(atom.getName()).app(")");
				ClassDeclWriter.writeFuncArgs(funcDecl, cgen);
				writeInitAndComma(cgen, type, iter, atom);
			}
			
		} else {
			
			boolean isStatic = variableDecl.isStatic();
			TypeDecl typeDecl = variableDecl.getTypeDecl();
			if(isStatic && (typeDecl == null)) cgen.current.append("static ");
			
			if(!type.isArray()) {
				TypeWriter.writeSpaced(type, cgen);
			} else {
				cgen.current.app(type.getName()).app(' ');
			}

			String typePrefix = isStatic && (typeDecl != null) ?
					(typeDecl.getType().getMangledName()) + "_" : "";
			
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				cgen.current.app(typePrefix).app(atom.getName());
				if(type.isArray()) for(int i = 0; i < type.getPointerLevel(); i++) {
					cgen.current.app("[]");
				}
				writeInitAndComma(cgen, type, iter, atom);
			}
			
		}
		
	}

	private static void writeInitAndComma(CGenerator cgen, Type type,
			Iterator<VariableDeclAtom> iter, VariableDeclAtom atom)
			throws IOException {
		if(atom.getExpression() != null) {
			cgen.current.app(" = ");
			atom.getExpression().accept(cgen);
		}
		if(iter.hasNext()) {
			cgen.current.app(", ");
			TypeWriter.writeStars(type, cgen);
		}
	}
	
}
