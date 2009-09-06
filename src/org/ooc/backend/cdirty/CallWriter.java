package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
		
		NodeList<Expression> args = functionCall.getArguments();
		cgen.current.app('(');
		if(functionCall.getReturnArg() != null) {
			functionCall.getReturnArg().accept(cgen);
			if(!args.isEmpty()) cgen.current.app(", ");
		}
		if(functionCall.isConstructorCall() && impl.getTypeDecl() instanceof ClassDecl) {
			cgen.current.app('(');
			impl.getTypeDecl().getInstanceType().accept(cgen);
			cgen.current.app(')');
			cgen.current.app(" this");
			if(!args.isEmpty()) cgen.current.app(", ");
		}
		writeCallArgs(args, impl, cgen);
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

	public static void writeCallArgs(NodeList<Expression> callArgs, FunctionDecl impl, CGenerator cgen) throws IOException {
		LinkedHashMap<String, GenericType> typeParams = impl.getGenericTypes();
		if(typeParams.isEmpty()) {
			Iterator<Expression> iter = callArgs.iterator();
			int argIndex = 0;
			while(iter.hasNext()) {
				writeCallArg(iter.next(), impl, argIndex, cgen);
				if(iter.hasNext()) cgen.current.app(", ");
				argIndex++;
			}
		} else {
			writeGenericCallArgs(callArgs, impl, typeParams, cgen);
		}
	}

	private static void writeCallArg(Expression callArg, FunctionDecl impl, int argIndex, CGenerator cgen)
			throws IOException {
		NodeList<Argument> implArgs = impl.getArguments();
		int realIndex = argIndex;
		if (impl.isMember() && !impl.isStatic()) realIndex++;
		boolean shouldCast = false;
		if(argIndex != -1 && realIndex < implArgs.size() && (impl.isExtern() || impl.getName().equals("init"))) {
			Argument arg = implArgs.get(realIndex);
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
			FunctionDecl impl, LinkedHashMap<String, GenericType> typeParams, CGenerator cgen) throws IOException {
		
		NodeList<Argument> implArgs = impl.getArguments();
		
		for(GenericType typeParam: typeParams.values()) {
			boolean done = false;
			int i = 0;
			for(Argument implArg: implArgs) {
				if(implArg.getType().getName().equals(typeParam.getName())) {
					Expression callArg = callArgs.get(i);
					if(callArg.getType().getRef() instanceof GenericType) {
						cgen.current.app(callArg.getType().getName()).app(", ");
					} else {
						cgen.current.app(callArg.getType().getName()).app("_class(), ");
					}
					done = true;
					break;
				}
				i++;
			}
			if(!done)
				throw new OocCompilationError(callArgs, cgen.module,
						"Couldn't find argument in "+callArgs+" to figure out generic type "+typeParam);
		}
		
		int argIndex = 0;
		Iterator<Expression> iter = callArgs.iterator();
		while(iter.hasNext()) {
			Expression expr = iter.next();
			Argument arg = implArgs.get(argIndex);
			GenericType typeParam = typeParams.get(arg.getType().getName());
			if(typeParam != null) {
				cgen.current.app("(Octet*) &");
			}
			writeCallArg(expr, impl, argIndex, cgen);
			if(iter.hasNext()) cgen.current.app(", ");
			argIndex++;
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
		writeCallArgs(memberCall.getArguments(), impl, cgen);
		
		cgen.current.app(')');
		
	}
	
}
