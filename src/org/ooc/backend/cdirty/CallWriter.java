package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeAccess;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.middle.OocCompilationError;

public class CallWriter {

	public static FunctionCall bypassPrelude = null;
	
	public static void write(FunctionCall functionCall, CGenerator cgen) throws IOException {

		FunctionDecl impl = functionCall.getImpl();
		writePrelude(cgen, impl, functionCall);
		
		if(functionCall.getName().equals("sizeof")) {
			if(functionCall.getArguments().size() != 1) {
				throw new OocCompilationError(functionCall, cgen.module,
						"sizeof needs exactly one argument! " + functionCall.getArguments().size()
						+ (functionCall.getArguments().size() > 1 ? " is too many|" : " isn't enough|"));
			}
			Expression arg = functionCall.getArguments().getFirst();
			if(!(arg instanceof VariableAccess)) {
				throw new OocCompilationError(arg, cgen.module,
						"You can only call sizeof() on a type! What are you doing?");
			}
			cgen.current.app("sizeof(");
			if(arg instanceof MemberAccess) {
				arg.accept(cgen);
			} else if(arg instanceof TypeAccess) {
				((TypeAccess) arg).getType().accept(cgen);
			} else {
				VariableAccess varAcc = (VariableAccess) arg;
				cgen.current.app(varAcc.getUnderName());
			}
			cgen.current.app(")");
			return;
		}
		
		if(functionCall.isConstructorCall()) {
			cgen.current.app(impl.getTypeDecl().getUnderName());
			if(impl.getTypeDecl() instanceof ClassDecl) {
				cgen.current.app("_init");
			} else{
				cgen.current.app("_new");
			}
			if(impl.getSuffix().length() > 0) cgen.current.app('_').app(impl.getSuffix());
		} else if(impl.isFromPointer()) {
			cgen.current.app(functionCall.getFullName());
		} else {
			impl.writeFullName(cgen.current);
		}
		
		cgen.current.app('(');
		writeCallArgs(functionCall, impl, cgen);
		cgen.current.app(')');
		
	}

	private static void writePrelude(CGenerator cgen, FunctionDecl impl, FunctionCall call)
			throws IOException {
		if(bypassPrelude != null && call == bypassPrelude) {
			bypassPrelude = null;
			return;
		}
		if(impl.getReturnType().isVoid()) {
			throw new OocCompilationError(call, cgen.module, "Trying to use void function "+call+" as an expression!");
		}
		if(impl.isExternWithName() && !impl.getReturnType().isVoid()) {
			cgen.current.app('(');
			impl.getReturnType().accept(cgen);
			cgen.current.app(") ");
		}
	}

	public static void writeCallArgs(FunctionCall functionCall, FunctionDecl impl, CGenerator cgen) throws IOException {
		writeCallArgs(functionCall, impl, true, cgen);
	}
	
	public static void writeCallArgs(FunctionCall functionCall, FunctionDecl impl, boolean isFirstParam, CGenerator cgen) throws IOException {
		NodeList<Expression> callArgs = functionCall.getArguments();
		
		boolean isFirst = isFirstParam;
		if(functionCall.getImpl().getReturnType().isGeneric()) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			if(functionCall.getReturnArg() == null) {
				cgen.current.app("NULL");
			} else {
				cgen.current.app("(uint8_t*) ");
				functionCall.getReturnArg().accept(cgen);
			}
		}
		if(functionCall.isConstructorCall() && impl.getTypeDecl() instanceof ClassDecl) {
			isFirst = false;
			cgen.current.app('(');
			impl.getTypeDecl().getInstanceType().accept(cgen);
			cgen.current.app(')');
			cgen.current.app(" this");
		}
		
		if(!impl.isGeneric()) {
			Iterator<Expression> iter = callArgs.iterator();
			int argIndex = impl.hasThis() ? 0 : -1;
			while(iter.hasNext()) {
				argIndex++;
				if(!isFirst) cgen.current.app(", ");
				isFirst = false;
				writeCallArg(iter.next(), impl, argIndex, cgen);
			}
		} else {
			writeGenericCallArgs(functionCall, impl, cgen, isFirst);
		}
	}

	private static void writeCallArg(Expression callArg, FunctionDecl impl, int argIndex, CGenerator cgen)
			throws IOException {
		NodeList<Argument> implArgs = impl.getArguments();
		boolean shouldCast = false;
		if(argIndex != -1 && argIndex < implArgs.size() && (impl.isExtern() || impl.getName().equals("init"))) {
			Argument arg = implArgs.get(argIndex);
			Type implType = arg.getType().getGroundType();
			Type callType = callArg.getType().getGroundType();
			shouldCast = !(arg instanceof VarArg) && !(callType.equals(implType));
			if(shouldCast) {
				cgen.current.app("((");
				arg.getType().accept(cgen);
				cgen.current.app(") (");
			}
		}
		if(callArg instanceof VariableAccess) {
			AccessWriter.write((Access) callArg, false, cgen);
		} else {
			callArg.accept(cgen);
		}
		if(shouldCast) cgen.current.app("))");
	}

	public static void writeGenericCallArgs(FunctionCall call,
			FunctionDecl impl, CGenerator cgen, boolean isFirstArg) throws IOException {
		
		boolean isFirst = isFirstArg;
		NodeList<Argument> implArgs = impl.getArguments();
		
		Iterator<TypeParam> implTypeParams = impl.getTypeParams().values().iterator();
		for(Expression expr: call.getTypeParams()) {
			TypeParam implTypeParam = implTypeParams.next();
			if(implTypeParam.isGhost()) continue;
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			expr.accept(cgen);
		}
		
		int argIndex = impl.hasThis() ? 0 : -1;
		Iterator<Expression> iter = call.getArguments().iterator();
		while(iter.hasNext()) {
			argIndex++;
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			Expression expr = iter.next();
			Argument implArg = implArgs.get(argIndex);
			if(implArg.getType().isGeneric()) {
				cgen.current.app("(uint8_t*) ");
			}
			writeCallArg(expr, impl, argIndex, cgen);
		}
		
	}

	public static void writeMember(MemberCall memberCall, CGenerator cgen) throws IOException {

		FunctionDecl impl = memberCall.getImpl();
		writePrelude(cgen, impl, memberCall);
		if(impl.isFromPointer()) {
			boolean isArrow = memberCall.getExpression().getType().getRef() instanceof ClassDecl;
			
			Expression expression = memberCall.getExpression();
			if(!isArrow && expression instanceof Dereference) {
				Dereference deref = (Dereference) expression;
				expression = deref.getExpression();
				isArrow = true;
			}
			expression.accept(cgen);
			
			if(isArrow) {
				cgen.current.app("->");
			} else {
				cgen.current.app(".");
			}
			cgen.current.app(memberCall.getName());
		} else {
			if(memberCall.isSuperCall()) {
				cgen.current.append(impl.getTypeDecl().getUnderName());
				cgen.current.append("_");
				cgen.current.app(memberCall.getImpl().getSuffixedName());
				cgen.current.append("_impl");
			} else {
				impl.writeFullName(cgen.current);
			}
		}
		
		cgen.current.app('(');
		
		TypeDecl typeDecl = impl.getTypeDecl();
		
		boolean isFirst = true;
		if(!impl.isStatic()) {
			if(!typeDecl.getInstanceType().equals(memberCall.getExpression().getType())) {
				cgen.current.app('(');
				typeDecl.getInstanceType().accept(cgen);
				cgen.current.app(") ");
			}
			if(!impl.isFromPointer()) {
				isFirst = false;
				memberCall.getExpression().accept(cgen);
			}
		}
		writeCallArgs(memberCall, impl, isFirst, cgen);
		
		cgen.current.app(')');
		
	}
	
}
