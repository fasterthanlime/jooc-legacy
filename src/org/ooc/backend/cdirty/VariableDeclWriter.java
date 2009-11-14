package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;

public class VariableDeclWriter {

	public static boolean write(VariableDecl variableDecl, CGenerator cgen) throws IOException {

		if(variableDecl.isExtern()) return false;
		
		if (variableDecl.isGlobal())
		{
			assert cgen.current == cgen.hw;
			cgen.current.app("extern ");
			writeGuts(variableDecl, cgen, false);
			cgen.current = cgen.cw;
			writeGuts(variableDecl, cgen, true);
			cgen.current.app(";\n");
			cgen.current = cgen.hw;
		}
		else
		{
			writeGuts(variableDecl, cgen, true);
		}
		
		return true;
		
	}
	
	private static void writeGuts(VariableDecl variableDecl, CGenerator cgen,
			boolean writeInitializer) throws IOException {
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
				writeInitAndComma(cgen, type, iter.hasNext(), atom, writeInitializer);
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
				writeInitAndComma(cgen, type, iter.hasNext(), atom, writeInitializer);
			}
			
		}
	}

	private static void writeInitAndComma(CGenerator cgen, Type type,
			boolean writeComma, VariableDeclAtom atom, boolean writeInitializer)
			throws IOException {
		if(writeInitializer) {
			if(atom.getExpression() != null) {
				cgen.current.app(" = ");
				atom.getExpression().accept(cgen);
			}
		}
		if(writeComma) {
			cgen.current.app(", ");
			TypeWriter.writeFinale(type.getGroundType(), cgen);
		}
	}
	
}
