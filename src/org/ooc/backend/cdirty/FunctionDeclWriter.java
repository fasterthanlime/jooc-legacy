package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.*;

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
				cgen.current.nl().app(cgen.module.getLoadFuncName()).app("();");
			}
			
			for(Line line: functionDecl.getBody()) {
				cgen.current.nl();
				line.accept(cgen);
			}
			cgen.current.closeSpacedBlock();
		}
		
	}

	public static void writeFuncPrototype(FunctionDecl functionDecl, CGenerator cgen) throws IOException {
		
		Type returnType = functionDecl.getReturnType();
		if(returnType instanceof FuncType) {
			TypeWriter.writeFuncPointerStart((FuncType) returnType, cgen);
		} else {
			TypeWriter.writeSpaced(returnType, cgen);
		}
		functionDecl.writeFullName(cgen.current);
		
		cgen.current.app('(');
		boolean isFirst = true;
		for(TypeParam param: functionDecl.getTypeParams()) {
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
		
		if(returnType instanceof FuncType) {
			TypeWriter.writeFuncPointerEnd((FuncType) returnType, cgen);
		}
		
	}
	
}
