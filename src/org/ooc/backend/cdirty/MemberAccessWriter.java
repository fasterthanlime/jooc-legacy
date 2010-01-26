package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.PotentiallyStatic;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableAccess;

public class MemberAccessWriter {

	public static void write(MemberAccess memberAccess, boolean doTypeParams, CGenerator cgen, int refOffset) throws IOException {
		
		// Allows to do things like "if(T == Int)"
		if(memberAccess.getExpression() instanceof VariableAccess) {
			VariableAccess varAcc = (VariableAccess) memberAccess.getExpression();
			if(varAcc.getRef() instanceof TypeDecl) {
				if(memberAccess.getName().equals("class")) {
					cgen.current.app(varAcc.getUnderName()).app("_class()");
					return;
				}
			}
		}
		
		// duplicated code with LocalAccessWriter: modularize!
		int refLevel = memberAccess.getRef().getType().getReferenceLevel();
		if(doTypeParams) {
			if(memberAccess.getType().isGeneric()) {
				refLevel++;
			}
		}
		refLevel += refOffset;
		
		TypeDecl refTypeDecl = memberAccess.getRef().getTypeDecl();
		boolean isStatic = ((PotentiallyStatic) memberAccess.getRef()).isStatic();
		
		if(memberAccess.getRef() instanceof FunctionDecl) {
			writeFuncAccess(memberAccess, cgen);
			return;
		}
		
		if(refLevel > 0) {
			cgen.current.app('(');
			for(int i = 0; i < refLevel; i++) {
				cgen.current.app('*');
			}
		}
		
		if(isStatic) {
			if(memberAccess.getRef().isExternWithName()) {
				cgen.current.app(memberAccess.getRef().getExternName());
				if(refLevel > 0) cgen.current.app(')');
				return;
			}
			
			cgen.current.app("((").app(refTypeDecl.getType().getUnderName())
				.app("Class*) ").app(refTypeDecl.getType().getUnderName())
				.app("_class())->").app(memberAccess.getName());
		} else {
			boolean isArrow = (refTypeDecl instanceof ClassDecl);
			boolean didDeref = false;
			
			Expression expression = memberAccess.getExpression();
			if(!isArrow && expression instanceof Dereference) {
				Dereference deref = (Dereference) expression;
				expression = deref.getExpression();
				isArrow = true;
				didDeref = true;
			}
			
			if(refTypeDecl.getType().equals(expression.getType())) {		
				expression.accept(cgen);
			} else {
				cgen.current.app("((");
				cgen.current.app(((TypeDecl) refTypeDecl.getInstanceType().getRef()).getUnderName());
				Type membExprType = memberAccess.getExpression().getType();
				TypeWriter.writeFinale(membExprType, cgen);
				if(didDeref) cgen.current.app("*");
				cgen.current.app(") ");
				expression.accept(cgen);
				cgen.current.app(')');
			}
			
			cgen.current.app(isArrow ? "->" : ".");
			LocalAccessWriter.write(memberAccess, false, cgen);
			if(refLevel > 0) cgen.current.app(')');
		}
		
	}

	private static void writeFuncAccess(MemberAccess memberAccess,
			CGenerator cgen) throws IOException {
		FunctionDecl funcDecl = (FunctionDecl) memberAccess.getRef();
		
		TypeDecl typeDecl = funcDecl.getTypeDecl();
		String typeName = typeDecl.getUnderName();
		
		cgen.current.app("((").app(typeName).app("Class *) ");
		memberAccess.getExpression().accept(cgen);
		cgen.current.app(")->").app(memberAccess.getName());
	}
	
}
