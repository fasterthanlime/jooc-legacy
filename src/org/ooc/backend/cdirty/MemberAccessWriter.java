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
		
		if(memberAccess.getExpression() instanceof VariableAccess) {
			VariableAccess varAcc = (VariableAccess) memberAccess.getExpression();
			if(varAcc.getRef() instanceof TypeDecl) {
				if(memberAccess.getName().equals("class")) {
					cgen.current.app(varAcc.getName()).app("_class()");
					return;
				}
			}
		}
		
		TypeDecl refTypeDecl = memberAccess.getRef().getTypeDecl();
		boolean isStatic = ((PotentiallyStatic) memberAccess.getRef()).isStatic();
		
		if(memberAccess.getRef() instanceof FunctionDecl) {
			FunctionDecl funcDecl = (FunctionDecl) memberAccess.getRef();
			
			TypeDecl typeDecl = funcDecl.getTypeDecl();
			String typeName = typeDecl.getUnderName();
			
			cgen.current.app("((").app(typeName).app("Class *) ");
			memberAccess.getExpression().accept(cgen);
			cgen.current.app(")->").app(memberAccess.getName());
			
			return;
		}
		
		if(isStatic) {

			if(memberAccess.getRef().isExtern() && memberAccess.getRef().getExternName().length() > 0) {
				cgen.current.app(memberAccess.getRef().getExternName());
				return;
			}
			cgen.current.app("((").app(refTypeDecl.getType().getUnderName())
				.app("Class*) ").app(refTypeDecl.getType().getName())
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
			
			if(isArrow) {
				cgen.current.app("->");
			} else {
				cgen.current.app('.');
			}
			LocalAccessWriter.write(memberAccess, false, cgen);
		
		}
		
	}
	
}
