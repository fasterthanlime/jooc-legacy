package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;

public class AccessWriter {

	public static void writeMember(MemberAccess memberAccess, CGenerator cgen) throws IOException {

		if(memberAccess.getExpression() instanceof VariableAccess) {
			VariableAccess varAcc = (VariableAccess) memberAccess.getExpression();
			if(varAcc.getRef() instanceof TypeDecl) {
				if(memberAccess.getName().equals("class")) {
					cgen.current.app(varAcc.getName()).app("_class()");
					return;
				}
			}
		}
		
		TypeDecl typeDecl = memberAccess.getRef().getTypeDecl();
		boolean isStatic = ((VariableDecl) memberAccess.getRef()).isStatic();
		
		if(isStatic) {

			if(memberAccess.getRef().isExtern() && !memberAccess.getRef().getExternName().isEmpty()) {
				cgen.current.app(memberAccess.getRef().getExternName());
				return;
			}
			cgen.current.app("((").app(typeDecl.getType().getMangledName())
				.app("Class*) ").app(typeDecl.getType().getMangledName())
				.app("_class())->").app(memberAccess.getName());
			
		} else {
		
			boolean isArrow = (typeDecl instanceof ClassDecl);
			
			Expression expression = memberAccess.getExpression();
			if(!isArrow && expression instanceof Dereference) {
				Dereference deref = (Dereference) expression;
				expression = deref.getExpression();
				isArrow = true;
			}
			
			if(typeDecl.getType().equals(memberAccess.getExpression().getType())) {		
				expression.accept(cgen);
			} else {
				cgen.current.app("((");
				typeDecl.getInstanceType().accept(cgen);
				cgen.current.app(')');
				expression.accept(cgen);
				cgen.current.app(')');
			}
			
			if(isArrow) {
				cgen.current.app("->");
			} else {
				cgen.current.app('.');
			}
			
			writeVariable(memberAccess, true, cgen);
		
		}
		
	}
	
	public static void writeVariable(VariableAccess variableAccess, boolean doTypeParams, CGenerator cgen) throws IOException {
		
		if(variableAccess.getRef() instanceof TypeDecl) {
			cgen.current.app(variableAccess.getName()).app("_class()");
			return;
		}
		
		int refLevel = variableAccess.getRef().getType().getReferenceLevel();
		
		if(doTypeParams) {
			Declaration ref = variableAccess.getRef().getType().getRef();
			if(ref instanceof GenericType) refLevel++;
		}
		
		if(refLevel > 0) {
			cgen.current.app('(');
			for(int i = 0; i < refLevel; i++) {
				cgen.current.app('*');
			}
		}
		cgen.current.app(variableAccess.getRef().getExternName(variableAccess));
		if(refLevel > 0) {
			cgen.current.app(')');
		}
		
	}

	public static void writeArray(ArrayAccess arrayAccess, CGenerator cgen) throws IOException {
		arrayAccess.getVariable().accept(cgen);
		cgen.current.app('[');
		arrayAccess.getIndex().accept(cgen);
		cgen.current.app(']');
	}
	
}
