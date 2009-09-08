package org.ooc.backend.cdirty;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.middle.OocCompilationError;

public class CallWriter {

	public static FunctionCall noCast = null;
	
	public static void write(FunctionCall functionCall, CGenerator cgen) throws IOException {

		FunctionDecl impl = functionCall.getImpl();
		writePrelude(cgen, impl, functionCall);
		
		if(functionCall.getName().equals("sizeof")) {
			Expression arg = functionCall.getArguments().getFirst();
			if(!(arg instanceof VariableAccess)) {
				throw new OocCompilationError(arg, cgen.module, "You can only call sizeof() on a type! What are you doing?");
			}
			VariableAccess varAcc = (VariableAccess) arg;
			cgen.current.app("sizeof(").app(varAcc.getName()).app(")");
			return;
		}
		
		if(functionCall.isConstructorCall()) {
			cgen.current.app(impl.getTypeDecl().getName());
			if(impl.getTypeDecl() instanceof ClassDecl) {
				cgen.current.app("_init");
			} else{
				cgen.current.app("_new");
			}
			if(!impl.getSuffix().isEmpty()) cgen.current.app('_').app(impl.getSuffix());
		} else if(impl.isFromPointer()) {
			cgen.current.app(functionCall.getName());
		} else {
			impl.writeFullName(cgen.current);
		}
		
		cgen.current.app('(');
		writeCallArgs(functionCall, impl, cgen);
		cgen.current.app(')');
		
	}

	private static void writePrelude(CGenerator cgen, FunctionDecl impl, FunctionCall call)
			throws IOException {
		if(noCast != null && call == noCast) {
			noCast = null;
			return;
		}
		if(impl.getReturnType().isVoid()) {
			throw new OocCompilationError(call, cgen.module, "Trying to use void function as an expression!");
		}
		if(impl.isExternWithName() && !impl.getReturnType().isVoid()) {
			cgen.current.app('(');
			impl.getReturnType().accept(cgen);
			cgen.current.app(") ");
		}
	}

	public static void writeCallArgs(FunctionCall functionCall, FunctionDecl impl, CGenerator cgen) throws IOException {
		NodeList<Expression> callArgs = functionCall.getArguments();
		
		boolean isFirst = true;
		if(functionCall.getReturnArg() != null) {
			if(!isFirst) {
				cgen.current.app(", ");
			} else isFirst = false;
			functionCall.getReturnArg().accept(cgen);
		}
		if(functionCall.isConstructorCall() && impl.getTypeDecl() instanceof ClassDecl) {
			if(!isFirst) {
				cgen.current.app(", ");
			} else isFirst = false;
			cgen.current.app('(');
			impl.getTypeDecl().getInstanceType().accept(cgen);
			cgen.current.app(')');
			cgen.current.app(" this");
		}
		
		if(!impl.isGeneric()) {
			Iterator<Expression> iter = callArgs.iterator();
			int argIndex = -1;
			while(iter.hasNext()) {
				argIndex++;
				if(!isFirst) {
					cgen.current.app(", ");
				} else isFirst = false;
				writeCallArg(iter.next(), impl, argIndex, cgen);
			}
		} else {
			writeGenericCallArgs(callArgs, impl, cgen, isFirst);
		}
	}

	private static void writeCallArg(Expression callArg, FunctionDecl impl, int argIndex, CGenerator cgen)
			throws IOException {
		NodeList<Argument> implArgs = impl.getArguments();
		boolean shouldCast = false;
		if(argIndex != -1 && argIndex < implArgs.size() && (impl.isExtern() || impl.getName().equals("init"))) {
			Argument arg = implArgs.get(argIndex);
			if(!(arg instanceof VarArg)) {
				shouldCast = true;
				cgen.current.app("((");
				arg.getType().accept(cgen);
				cgen.current.app(") (");
			}
		}
		callArg.accept(cgen);
		if(shouldCast) cgen.current.app("))");
	}

	public static void writeGenericCallArgs(NodeList<Expression> callArgs,
			FunctionDecl impl, CGenerator cgen, boolean isFirstArg) throws IOException {
		
		boolean isFirst = isFirstArg;
		NodeList<Argument> implArgs = impl.getArguments();
		
		for(GenericType typeParam: impl.getGenericTypes().values()) {
			isFirst = writeGenType(callArgs, impl, cgen, isFirst, typeParam);
		}
		
		int argIndex = impl.hasThis() ? 1 : 0;
		Iterator<Expression> iter = callArgs.iterator();
		while(iter.hasNext()) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			Expression expr = iter.next();
			Argument arg = implArgs.get(argIndex);
			GenericType genericType = impl.getGenericType(arg.getType().getName());
			if(genericType != null) cgen.current.app("(Octet*) &");
			writeCallArg(expr, impl, argIndex, cgen);
			argIndex++;
		}
		
	}

	private static boolean writeGenType(NodeList<Expression> callArgs,
			FunctionDecl impl, CGenerator cgen, boolean isFirstParam,
			GenericType typeParam) throws IOException, OocCompilationError,
			EOFException {
		boolean isFirst = isFirstParam;
		boolean done = false;
		int i = -1;
		Iterator<Argument> iter = impl.getThisLessArgsIter();
		while(iter.hasNext()) {
			i++;
			Argument implArg = iter.next();
			if(implArg.getType().getName().equals(typeParam.getName())) {
				if(!isFirst) cgen.current.app(", ");
				isFirst = false;
				Expression callArg = callArgs.get(i);
				if(callArg.getType().getRef() instanceof GenericType) {
					cgen.current.app(callArg.getType().getName());
				} else {
					cgen.current.app(callArg.getType().getName()).app("_class()");
				}
				done = true;
				break;
			}
			if(implArg.getName().equals(typeParam.getName())) {
				if(!isFirst) cgen.current.app(", ");
				isFirst = false;
				cgen.current.app(implArg.getName());
				done = true;
				break;
			}
		}
		if(!done)
			throw new OocCompilationError(callArgs, cgen.module,
					"Couldn't find argument in "+callArgs+" to figure out generic type "+typeParam);
		return isFirst;
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
			impl.writeFullName(cgen.current);
		}
		
		cgen.current.app('(');
		
		TypeDecl typeDecl = impl.getTypeDecl();
		if(!typeDecl.getInstanceType().equals(memberCall.getExpression().getType())) {
			cgen.current.app('(');
			typeDecl.getInstanceType().accept(cgen);
			cgen.current.app(") ");
		}
		if(!impl.isStatic() && !impl.isFromPointer()) {
			memberCall.getExpression().accept(cgen);
			if(!memberCall.getArguments().isEmpty()) cgen.current.app(", ");
		}
		writeCallArgs(memberCall, impl, cgen);
		
		cgen.current.app(')');
		
	}
	
}
