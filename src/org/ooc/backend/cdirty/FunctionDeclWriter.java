package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;

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
				// FIXME what if we want no gc?
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
		writeFuncPrototype(functionDecl, cgen, null);
	}
	
	
	public static void writeFuncPrototype(FunctionDecl functionDecl, CGenerator cgen, String additionalSuffix) throws IOException {
		
		if(functionDecl.isInline()) cgen.current.append("inline ");
			
		Type returnType = functionDecl.getReturnType();
		if (returnType.getRef() instanceof GenericType) {
			cgen.current.append("void ");
		} else if(returnType instanceof FuncType) {
			TypeWriter.writeFuncPointerStart((FunctionDecl) returnType.getRef(), cgen);
		} else {
			TypeWriter.writeSpaced(returnType, cgen);
		}
		functionDecl.writeFullName(cgen.current);
		if(additionalSuffix != null) cgen.current.append(additionalSuffix);
		
		writeFuncArgs(functionDecl, cgen);
		
		if(returnType instanceof FuncType) {
			TypeWriter.writeFuncPointerEnd((FunctionDecl) returnType.getRef(), cgen);
		}
		
	}
	
	public static void writeFuncArgs(FunctionDecl functionDecl, CGenerator cgen) throws IOException {
		writeFuncArgs(functionDecl, ArgsWriteMode.FULL, null, cgen);
	}
	
	public static enum ArgsWriteMode {
		FULL,
		NAMES_ONLY,
		TYPES_ONLY
	}
	
	public static void writeFuncArgs(FunctionDecl functionDecl, ArgsWriteMode mode,
			TypeDecl baseType, CGenerator cgen) throws IOException {
		
		cgen.current.app('(');
		boolean isFirst = true;
		
		Iterator<Argument> iter = functionDecl.getArguments().iterator();
		if(functionDecl.hasThis()) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			Argument arg = iter.next();
			if(mode == ArgsWriteMode.NAMES_ONLY) {
				if(baseType != null) {
					cgen.current.app("(");
					baseType.getType().accept(cgen);
					cgen.current.app(") ");
				}
				cgen.current.app(arg.getName());
			} else if(mode == ArgsWriteMode.TYPES_ONLY) {
				arg.getType().accept(cgen);
			} else {
				arg.accept(cgen);
			}
		}
		
		Type returnType = functionDecl.getReturnType();
		if(returnType.getRef() instanceof GenericType) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			if(mode == ArgsWriteMode.NAMES_ONLY) {
				cgen.current.app(functionDecl.getReturnArg().getName());
			} else if(mode == ArgsWriteMode.TYPES_ONLY) {
				functionDecl.getReturnArg().getType().accept(cgen);
			} else {
				functionDecl.getReturnArg().accept(cgen);
			}
		}
		for(GenericType param: functionDecl.getGenericTypes().values()) {
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			if(mode == ArgsWriteMode.NAMES_ONLY) {
				cgen.current.app(param.getArgument().getName());
			} else if(mode == ArgsWriteMode.TYPES_ONLY) {
				param.getArgument().getType().accept(cgen);
			} else {
				param.getArgument().accept(cgen);
			}
		}
		
		while(iter.hasNext()) {
			Argument arg = iter.next();
			if(!isFirst) cgen.current.app(", ");
			isFirst = false;
			if(mode == ArgsWriteMode.NAMES_ONLY) {
				cgen.current.app(arg.getName());
			} else if(mode == ArgsWriteMode.TYPES_ONLY) {
				arg.getType().accept(cgen);
			} else {
				arg.accept(cgen);
			}
		}
		cgen.current.app(')');
	}
	
}
