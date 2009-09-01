package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.GenericType;

public class FunctionDeclWriter {

	public static void write(FunctionDecl functionDecl, CGenerator cgen) throws IOException {
		
		if(functionDecl.isProto()) {
			cgen.current = cgen.hw;
			cgen.current.nl().app("extern ");
			writeFuncPrototype(functionDecl, cgen);
			cgen.current.app(';');
		} else if(!functionDecl.isExtern() && !functionDecl.isAbstract()) {
			cgen.current = cgen.hw;
			cgen.current.nl();
			writeFuncPrototype(functionDecl, cgen);
			cgen.current.app(';');
		
			cgen.current = cgen.cw;
			writeFuncPrototype(functionDecl, cgen);
			cgen.current.openBlock();
			
			if(functionDecl.isEntryPoint()) {
				cgen.current.nl().app("GC_INIT();");
				cgen.current.nl().app(cgen.module.getLoadFunc().getName()).app("();");
			}
			
			for(Line line: functionDecl.getBody()) {
				line.accept(cgen);
			}
			cgen.current.closeSpacedBlock();
		}
		
	}

	public static void writeFuncPrototype(FunctionDecl functionDecl, CGenerator cgen) throws IOException {
		
		Type returnType = functionDecl.getReturnType();
		if (returnType.getRef() instanceof GenericType) {
			cgen.current.append("void ");
		} else if(returnType instanceof FuncType) {
			TypeWriter.writeFuncPointerStart((FunctionDecl) returnType.getRef(), cgen);
		} else {
			TypeWriter.writeSpaced(returnType, cgen);
		}
		functionDecl.writeFullName(cgen.current);
		
		writeFuncArgs(functionDecl, cgen);
		
		if(returnType instanceof FuncType) {
			TypeWriter.writeFuncPointerEnd((FunctionDecl) returnType.getRef(), cgen);
		}
		
	}
	
	public static void writeFuncArgs(FunctionDecl functionDecl,
			CGenerator cgen) throws IOException {
		cgen.current.app('(');
		boolean isFirst = true;
		Type returnType = functionDecl.getReturnType();
		if(returnType.getRef() instanceof GenericType) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			functionDecl.getReturnArg().accept(cgen);
		}
		for(GenericType param: functionDecl.getGenericTypes().values()) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			param.getArgument().accept(cgen);
		}
		
		for(Argument arg: functionDecl.getArguments()) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			arg.accept(cgen);
		}
		cgen.current.app(')');
	}
	
}
