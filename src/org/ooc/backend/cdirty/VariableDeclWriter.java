package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

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
		if(type.getName().equals("Func")) {
			
			FunctionDecl funcDecl = (FunctionDecl) type.getRef();
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				TypeWriter.writeSpaced(funcDecl.getReturnType(), cgen);
				cgen.current.app("(*").app(atom.getName()).app(")");
				FunctionDeclWriter.writeFuncArgs(funcDecl, cgen);
				writeInitAndComma(cgen, type, iter.hasNext(), atom);
			}
			
		} else {
			
			boolean isStatic = variableDecl.isStatic();
			TypeDecl typeDecl = variableDecl.getTypeDecl();
			if(isStatic && (typeDecl == null)) cgen.current.append("static ");
			
			if(!type.isArray()) {
				boolean isConst = type.isConst();
				type.setConst(false);
				TypeWriter.writeSpaced(type, cgen);
				type.setConst(isConst);
			} else {
				cgen.current.app(type.getUnderName()).app(' ');
			}
			
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				if(type.isArray()) {
					TypeWriter.writePreFinale(type, cgen);
				}
				cgen.current.app(atom.getName());
				if(type.isArray()) {
					TypeWriter.writePostFinale(type, cgen);
				}
				writeInitAndComma(cgen, type, iter.hasNext(), atom);
			}
			
		}
		
	}

	private static void writeInitAndComma(CGenerator cgen, Type type,
			boolean writeComma, VariableDeclAtom atom)
			throws IOException {
		if(atom.getExpression() != null) {
			cgen.current.app(" = ");
			atom.getExpression().accept(cgen);
		}
		if(writeComma) {
			cgen.current.app(", ");
			TypeWriter.writeFinale(type, cgen);
		}
	}
	
}
