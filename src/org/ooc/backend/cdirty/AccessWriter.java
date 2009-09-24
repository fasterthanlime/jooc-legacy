package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.PotentiallyStatic;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeAccess;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.VariableAccess;

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
		
		TypeDecl refTypeDecl = memberAccess.getRef().getTypeDecl();
		boolean isStatic = ((PotentiallyStatic) memberAccess.getRef()).isStatic();
		
		if(memberAccess.getRef() instanceof FunctionDecl) {
			FunctionDecl funcDecl = (FunctionDecl) memberAccess.getRef();
			TypeDecl typeDecl = funcDecl.getTypeDecl();
			String typeName = typeDecl.getUnderName();
			if(typeDecl instanceof ClassDecl) {
				ClassDecl classDecl = (ClassDecl) typeDecl;
				ClassDecl baseClass = classDecl.getBaseClass(funcDecl);
				typeName = baseClass.getUnderName();
			}
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
			writeVariable(memberAccess, false, cgen);
		
		}
		
	}

	public static void writeVariable(VariableAccess variableAccess, boolean doTypeParams, CGenerator cgen) throws IOException {
		
		if(variableAccess.getRef() instanceof TypeDecl && !(variableAccess.getRef() instanceof TypeParam)) {
			cgen.current.app(variableAccess.getName()).app("_class()");
			return;
		}
		
		int refLevel = variableAccess.getRef().getType().getReferenceLevel();
		
		if(doTypeParams) {
			if(variableAccess.getType().isGeneric()) {
				refLevel++;
			}
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
	
	public static void write(Access access, boolean doTypeParams, CGenerator cgen) throws IOException {
		if(access instanceof TypeAccess) System.out.println("Should write typeAccess to "+access);
		if(access instanceof ArrayAccess) writeArray((ArrayAccess) access, cgen);
		else if(access instanceof MemberAccess) writeMember((MemberAccess) access, cgen);
		else writeVariable((VariableAccess) access, doTypeParams, cgen);
	}
	
}
