package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.VarArg;

public class FunctionDeclWriter {

	public static void write(FunctionDecl functionDecl, CGenerator cgen) throws IOException {
		
		if(functionDecl.isProto()) {
			
			// FW
			cgen.current = cgen.fw;
			
			if(functionDecl.getVersion() != null) {
				VersionBlockWriter.writeVersionBlockStart(functionDecl.getVersion(), cgen);
			}
			
			cgen.current.nl().app("extern ");
			writeFuncPrototype(functionDecl, cgen);
			cgen.current.app(';');
			
			if(functionDecl.getVersion() != null) {
				VersionBlockWriter.writeVersionBlockEnd(cgen);
			}
			
		} else if(functionDecl.isExtern()) {

			if(functionDecl.isExternWithName()) {
			
				// FW
				cgen.current = cgen.fw;
			
				if(functionDecl.getVersion() != null) {
					VersionBlockWriter.writeVersionBlockStart(functionDecl.getVersion(), cgen);
				}
				
				cgen.current.nl().app("#ifndef ");
				functionDecl.writeFullName(cgen.current);
				
				cgen.current.nl().app("#define ");
				functionDecl.writeFullName(cgen.current);
				
				cgen.current.app(' ').app(functionDecl.getExternName());
				
				cgen.current.nl().app("#endif");
				
				if(functionDecl.getVersion() != null) {
					VersionBlockWriter.writeVersionBlockEnd(cgen);
				}
			
			}
			
		} else if(!functionDecl.isAbstract()) {
			
			// FW
			cgen.current = cgen.fw;
			
			if(functionDecl.isInline()) {
				writeFullBody(functionDecl, cgen);
			} else {
				writeFullPrototype(functionDecl, cgen);
			}
		
			// CW
			cgen.current = cgen.cw;
			
			if(!functionDecl.isInline()) {
				writeFullBody(functionDecl, cgen);
			}
		}
		
	}

	private static void writeFullPrototype(FunctionDecl functionDecl,
			CGenerator cgen) throws IOException {
		if(functionDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockStart(functionDecl.getVersion(), cgen);
		}
		
		cgen.current.nl();
		writeFuncPrototype(functionDecl, cgen);
		cgen.current.app(';');
		
		if(functionDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockEnd(cgen);
		}
	}

	private static void writeFullBody(FunctionDecl functionDecl, CGenerator cgen)
			throws IOException {
		
		if(functionDecl.isInline()) {
			System.out.println("Writing full body of "+functionDecl+", which is inline, and cgen.current = "+
					(cgen.current == cgen.cw ? "cw" : (cgen.current == cgen.hw ? "hw" : (cgen.current == cgen.fw ? "fw" : "unknown"))));
		}
		
		if(functionDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockStart(functionDecl.getVersion(), cgen);
		}
		
		cgen.current.nl();
		writeFuncPrototype(functionDecl, cgen);
		cgen.current.app(' ').openBlock();
		
		if(functionDecl.isEntryPoint(cgen.params)) {
			if(cgen.params.enableGC) {
				cgen.current.nl().app("GC_INIT();");
			}
			cgen.current.nl().app(cgen.module.getLoadFunc().getName()).app("();");
		}
		
		for(Line line: functionDecl.getBody()) {
			line.accept(cgen);
		}
		cgen.current.closeSpacedBlock();
		
		if(functionDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockEnd(cgen);
		}
	}

	public static void writeFuncPrototype(FunctionDecl functionDecl, CGenerator cgen) throws IOException {
		writeFuncPrototype(functionDecl, cgen, null);
	}
	
	
	public static void writeFuncPrototype(FunctionDecl functionDecl, CGenerator cgen, String additionalSuffix) throws IOException {
		
		if(functionDecl.isInline()) {
			cgen.current.append("inline static ");
		}
			
		Type returnType = functionDecl.getReturnType();
		if (returnType.getRef() instanceof TypeParam) {
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
		if(functionDecl.hasThis() && iter.hasNext()) {
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
		if(returnType.getRef() instanceof TypeParam) {
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
		for(TypeParam param: functionDecl.getTypeParams().values()) {
			if(param.isGhost()) continue;
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
				if(arg instanceof VarArg) {
					cgen.current.app("...");
				} else {
					arg.getType().accept(cgen);
				}
			} else {
				arg.accept(cgen);
			}
		}
		cgen.current.app(')');
	}
	
}
