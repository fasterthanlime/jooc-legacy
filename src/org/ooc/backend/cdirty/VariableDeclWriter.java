package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ArrayLiteral;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableDecl;

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
			// the initializer should be written somewhere else if it's a member
			writeGuts(variableDecl, cgen, !variableDecl.isMember());
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
			TypeWriter.writeSpaced(funcDecl.getReturnType(), cgen);
			cgen.current.app("(*");
			variableDecl.writeFullName(cgen.current);
			cgen.current.app(")");
			FunctionDeclWriter.writeFuncArgs(funcDecl, cgen);
			writeInitAndComma(cgen, type, variableDecl, writeInitializer);
			
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
				Expression firstExpr = variableDecl.getExpression();
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
			
			if(type.isArray()) {
				TypeWriter.writePreFinale(type, cgen);
			}
			variableDecl.writeFullName(cgen.current);
			if(type.isArray()) {
				if(variableDecl.getExpression() instanceof ArrayLiteral) {
					TypeWriter.writePostFinale(type, cgen, (ArrayLiteral) variableDecl.getExpression());
				} else {
					TypeWriter.writePostFinale(type, cgen);
				}
			}
			writeInitAndComma(cgen, type, variableDecl, writeInitializer);
		}
	}

	private static void writeInitAndComma(CGenerator cgen, Type type, VariableDecl vDecl, boolean writeInitializer)
			throws IOException {
		if(writeInitializer) {
			if(vDecl.getExpression() != null) {
				cgen.current.app(" = ");
				vDecl.getExpression().accept(cgen);
			}/* else if(vDecl.getType().getClassification() == Type.Classification.NUMBER && vDecl.getType().isFlat()) {
				cgen.current.app(" = 0");
			} else if(vDecl.getType().getClassification() == Type.Classification.POINTER && !vDecl.getType().isArray()) {
				cgen.current.app(" = NULL");
			}*/
			// FIXME: currently breaks os/Time, but is interesting nonetheless..
		}
	}
	
}
