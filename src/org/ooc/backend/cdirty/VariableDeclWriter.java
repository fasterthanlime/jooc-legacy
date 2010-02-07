package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.ArrayLiteral;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;

public class VariableDeclWriter {

	public static boolean write(VariableDecl variableDecl, CGenerator cgen) throws IOException {

		if(variableDecl.isExtern() && !variableDecl.isProto()) {
			return false;
		}
		
		if(variableDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockStart(variableDecl.getVersion(), cgen);
			cgen.current.nl();
		}
		
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
		
		if(variableDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockEnd(cgen);
			cgen.current.nl();
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
				cgen.current.app("(*").app(variableDecl.getFullName(atom)).app(")");
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
				Type ground = type.getGroundType().clone();
				Expression firstExpr = variableDecl.getAtoms().getFirst().getExpression();
				if(firstExpr instanceof ArrayLiteral) {
					ArrayLiteral lit = (ArrayLiteral) firstExpr;
					ground.setPointerLevel(ground.getPointerLevel() - lit.getDepth());
					//System.out.println("depointerized of "+lit.getDepth());
				} else {
					ground.setPointerLevel(ground.getPointerLevel() - 1);
				}
				TypeWriter.write(ground, cgen, true, false);
				TypeWriter.writeFinale(ground, cgen);
				cgen.current.app(' ');
			}
			
			Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
			while(iter.hasNext()) {
				VariableDeclAtom atom = iter.next();
				if(type.isArray()) {
					TypeWriter.writePreFinale(type, cgen);
				}
				cgen.current.app(variableDecl.getFullName(atom));
				if(type.isArray()) {
					if(atom.getExpression() instanceof ArrayLiteral) {
						TypeWriter.writePostFinale(type, cgen, (ArrayLiteral) atom.getExpression());
					} else {
						TypeWriter.writePostFinale(type, cgen);
					}
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
			if(!type.isArray()) {
				TypeWriter.writeFinale(type.getGroundType(), cgen);
			}
		}
	}
	
}
