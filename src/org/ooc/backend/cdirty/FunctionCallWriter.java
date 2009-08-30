package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.VarArg;
import org.ooc.middle.OocCompilationError;

public class FunctionCallWriter {

	public static FunctionCall noCast = null;
	
	public static void write(FunctionCall functionCall, CGenerator cgen) throws IOException {

		FunctionDecl impl = functionCall.getImpl();
		writePrelude(cgen, impl, functionCall);
		
		if(functionCall.isConstructorCall()) {
			cgen.current.app(impl.getTypeDecl().getName());
			if(impl.getTypeDecl() instanceof ClassDecl) {
				cgen.current.app("_construct");
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
		if(impl.isExternWithName() && !impl.getReturnType().isVoid()) {
			cgen.current.app('(');
			impl.getReturnType().accept(cgen);
			cgen.current.app(") ");
		}
	}

	public static void writeCallArgs(NodeList<Expression> callArgs, FunctionDecl impl, CGenerator cgen) throws IOException {
		List<TypeParam> typeParams = impl.getTypeParams();
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
		int realIndex = (impl.isMember() && !impl.isStatic()) ? argIndex + 1 : argIndex;
		if(argIndex != -1 && realIndex < implArgs.size() && impl.isExternWithName()) {
			Argument arg = implArgs.get(realIndex);
			if(!(arg instanceof VarArg)) {
				cgen.current.app('(');
				arg.getType().accept(cgen);
				cgen.current.app(") ");
			}
		}
		callArg.accept(cgen);
	}

	public static void writeGenericCallArgs(NodeList<Expression> callArgs,
			FunctionDecl impl, List<TypeParam> typeParams, CGenerator cgen) throws IOException {
		
		NodeList<Argument> implArgs = impl.getArguments();
		
		for(TypeParam typeParam: typeParams) {
			boolean done = false;
			int i = 0;
			for(Argument implArg: implArgs) {
				if(implArg.getType().getName().equals(typeParam.getName())) {
					Expression callArg = callArgs.get(i);
					cgen.current.append(callArg.getType().getName()+"_class(), ");
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
			for(TypeParam param: typeParams) {
				if(arg.getType().getName().equals(param.getName())) {
					cgen.current.app("&");
				}
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

	public static void writeInst(Instantiation inst, CGenerator cgen) throws IOException {
		FunctionDecl impl = inst.getImpl();
		writePrelude(cgen, impl, inst);
		impl.writeFullName(cgen.current);
		cgen.current.app('(');
		writeCallArgs(inst.getArguments(), impl, cgen);
		cgen.current.app(')');
	}
	
}
