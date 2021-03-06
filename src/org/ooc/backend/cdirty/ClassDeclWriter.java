package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.ooc.backend.cdirty.FunctionDeclWriter.ArgsWriteMode;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableDecl;

public class ClassDeclWriter {
	
	public static final String LANG_PREFIX = "lang_types__";
	public static final String CLASS_NAME = LANG_PREFIX+"Class";
	
	public static void write(ClassDecl classDecl, CGenerator cgen) throws IOException {

		TypeWriter.doStruct = true;
		
		/* -fwd.h file */
		cgen.current = cgen.fw;
		if(classDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockStart(classDecl.getVersion(), cgen);
		}
		
		writeMemberFuncPrototypes(classDecl, cgen);
		
		if(classDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockEnd(cgen);
		}
		
		/* .h file */
		cgen.current = cgen.hw;
		if(classDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockStart(classDecl.getVersion(), cgen);
		}
		
		writeObjectStruct(classDecl, cgen);
		writeClassStruct(classDecl, cgen);
		
		if(classDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockEnd(cgen);
		}
		
		/* .c file */
		cgen.current = cgen.cw;
		if(classDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockStart(classDecl.getVersion(), cgen);
		}
		cgen.current.nl();
		
		writeInstanceImplFuncs(classDecl, cgen);
		writeClassGettingFunction(classDecl, cgen);
		writeInstanceVirtualFuncs(classDecl, cgen);
		writeStaticFuncs(classDecl, cgen);
		
		if(classDecl.getVersion() != null) {
			VersionBlockWriter.writeVersionBlockEnd(cgen);
		}
		
		TypeWriter.doStruct = false;
		
		cgen.current.nl();
		
	}

	public static void writeStaticFuncs(ClassDecl cDecl, CGenerator cgen) throws IOException {

		for (FunctionDecl decl : cDecl.getFunctions()) {
            cgen.current = cgen.cw;

			if (!decl.isStatic() || decl.isExtern()) {
				if(decl.isExtern()) {
					FunctionDeclWriter.write(decl, cgen);
				}
				continue;
			}

			cgen.current.nl();
			FunctionDeclWriter.writeFuncPrototype(decl, cgen);
			cgen.current.openBlock();
			
            if(decl.getName() == ClassDecl.LOAD_FUNC_NAME) {
                ClassDecl superRef = cDecl.getSuperRef();
                if(superRef != null) {
	            	FunctionDecl superLoad = superRef.getFunction(ClassDecl.LOAD_FUNC_NAME, null, null);
	            	if(superLoad != null) {
	            		cgen.current.nl().app(superLoad.getFullName()).app("();");
	            	}
                }
            	for(VariableDecl vDecl: cDecl.getVariables()) {
            		if(!vDecl.isStatic()) continue;
        			if(vDecl.getExpression() == null) continue;
    				cgen.current.nl().app("((").app(cDecl.getUnderName()).app("Class*) ").app(cDecl.getUnderName()).app("_class())->").app(vDecl.getName()).app(" = ");
    				vDecl.getExpression().accept(cgen);
    				cgen.current.app(';');
				}
            }
			
			decl.getBody().accept(cgen);
			cgen.current.closeSpacedBlock();

		}
	}

	public static void writeInstanceVirtualFuncs(ClassDecl classDecl,
			CGenerator cgen) throws IOException {

		for (FunctionDecl decl : classDecl.getFunctions()) {

			if (decl.isStatic() || decl.isFinal())
				continue;

			// yup, that's right baby.
			cgen.current = decl.isInline() ? cgen.hw : cgen.cw;
			
			cgen.current.nl();
			FunctionDeclWriter.writeFuncPrototype(decl, cgen);
			cgen.current.openSpacedBlock();

			ClassDecl baseClass = classDecl.getBaseClass(decl);
			
			if (decl.hasReturn()) {
				cgen.current.app("return (");
				decl.getReturnType().accept(cgen);
				cgen.current.app(")");
			}
			cgen.current.app("((").app(baseClass.getUnderName()).app(
					"Class *)((lang_types__Object *)this)->class)->");
			decl.writeSuffixedName(cgen.current);

			FunctionDeclWriter.writeFuncArgs(decl, ArgsWriteMode.NAMES_ONLY, baseClass, cgen);
			cgen.current.app(";").closeSpacedBlock();

		}
	}

	public static void writeInstanceImplFuncs(ClassDecl classDecl,
			CGenerator cgen) throws IOException {

		// Non-static (ie. instance) functions
		for (FunctionDecl decl : classDecl.getFunctions()) {
			if (decl.isStatic() || decl.isAbstract() || (decl.isExternWithName())) {
				continue;
			}
			
			// yup, that's right baby.
			cgen.current = decl.isInline() ? cgen.hw : cgen.cw;
			
			FunctionDeclWriter.writeFuncPrototype(decl, cgen, decl.isFinal() ? null : "_impl");
			cgen.current.openBlock();
			if(decl.getName().equals(ClassDecl.DEFAULTS_FUNC_NAME)) {
				if(classDecl.getSuperName().length() > 0) {
					cgen.current.nl().app(classDecl.getSuperRef().getUnderName()).app('_').app(ClassDecl.DEFAULTS_FUNC_NAME)
						.app("_impl((").app(classDecl.getSuperRef().getUnderName()).app(" *) this);");
				}
				for(VariableDecl vDecl: classDecl.getVariables()) {
					if(vDecl.isStatic()) continue;
					if(vDecl.getExpression() == null) continue;
					cgen.current.nl().app("this->").app(vDecl.getName()).app(" = ");
					vDecl.getExpression().accept(cgen);
					cgen.current.app(';');
				}
			}
			
			decl.getBody().accept(cgen);
			cgen.current.closeSpacedBlock();
		}

	}

	public static void writeClassGettingFunction(ClassDecl classDecl,
			CGenerator cgen) throws IOException {
		cgen.current.app(CLASS_NAME).app(" *").app(classDecl.getUnderName()).app("_class()")
				.openSpacedBlock();
		if (classDecl.getSuperName().length() > 0)
			cgen.current.app("static ").app("bool __done__ = false;").nl();
		cgen.current.app("static ").app(classDecl.getUnderName()).app(
				"Class class = ");
		
		writeClassStructInitializers(classDecl, classDecl, new HashSet<FunctionDecl>(), cgen);
		
		cgen.current.app(';');
		cgen.current.nl().app(CLASS_NAME).app(" *classPtr = (").app(CLASS_NAME).app(" *) &class;");
		if (classDecl.getSuperName().length() > 0) {
			cgen.current.nl().app("if(!__done__)").openBlock().nl().app(
					"__done__ = true;").nl().app("classPtr->super = ").app(
					classDecl.getSuperRef().getUnderName()).app("_class();").closeBlock();
		}

		cgen.current.nl().app("return classPtr;").closeSpacedBlock();
	}

	/**
	 * Write class initializers
	 * @param parentClass 
	 */
	public static void writeClassStructInitializers(ClassDecl parentClass,
			ClassDecl realClass, Set<FunctionDecl> done, CGenerator cgen) throws IOException {

		cgen.current.openBlock();

		if (!parentClass.isRootClass() && parentClass.getSuperName().length() > 0) {
			writeClassStructInitializers(parentClass.getSuperRef(), realClass, done, cgen);
		} else {
			cgen.current.openBlock();
			cgen.current.nl().app(".instanceSize = ").app("sizeof(").app(
					realClass.getUnderName()).app("),");
			cgen.current.nl().app(".size = ").app("sizeof(void*),");
			cgen.current.nl().app(".name = ").app('"').app(realClass.getName())
					.app("\",");
			cgen.current.closeBlock().app(',');
		}

		for (FunctionDecl parentDecl : parentClass.getFunctions()) {
			if(done.contains(parentDecl) && !parentDecl.getName().equals("init")) {
				continue;
			}
			
			FunctionDecl realDecl = null;
			if(realClass != parentClass && !parentDecl.getName().equals("init")) {
				realDecl = realClass.getFunction(parentDecl.getName(), parentDecl.getSuffix(), null, true, 0, null);
				if(realDecl != parentDecl) {
					if(done.contains(realDecl)) {
						continue;
					}
					done.add(realDecl);
				}
			}
			
			if (parentDecl.isStatic() || parentDecl.isFinal() || (realDecl == null && parentDecl.isAbstract())) {
				writeDesignatedInit(parentDecl, realDecl, false, cgen);
			} else {
				writeDesignatedInit(parentDecl, realDecl, true, cgen);
			}

		}

		cgen.current.closeBlock();
		if (realClass != parentClass)
			cgen.current.app(',');
	}

	public static void writeMemberFuncPrototypes(ClassDecl classDecl,
			CGenerator cgen) throws IOException {

		cgen.current.nl().app(CLASS_NAME).app(" *").app(classDecl.getUnderName()).app("_class();").nl();
		for (FunctionDecl decl : classDecl.getFunctions()) {

			if(decl.isExtern()) {
				continue;
			}
			
			cgen.current.newLine();
			FunctionDeclWriter.writeFuncPrototype(decl, cgen, null);
			cgen.current.app(';');
			if(!decl.isStatic() && !decl.isAbstract() && !decl.isFinal()) {
				cgen.current.newLine();
				FunctionDeclWriter.writeFuncPrototype(decl, cgen, "_impl");
				cgen.current.app(';');
			}
			
		}
		cgen.current.nl();
	}

	public static void writeFunctionDeclPointer(FunctionDecl decl, boolean doName, CGenerator cgen)
			throws IOException {
		if(decl.hasReturn()) decl.getReturnType().accept(cgen);
		else Type.getVoid().accept(cgen);
		
		cgen.current.app(" (*");
		if(doName) decl.writeSuffixedName(cgen.current);
		cgen.current.app(")");
		
		FunctionDeclWriter.writeFuncArgs(decl, ArgsWriteMode.TYPES_ONLY, null, cgen);
	}

	public static void writeClassStruct(ClassDecl classDecl, CGenerator cgen)
			throws IOException {

		cgen.current.nl().app("struct _").app(classDecl.getUnderName()).app("Class")
				.openSpacedBlock();
		if (classDecl.isRootClass()) {
			cgen.current.app("struct _").app(CLASS_NAME).app(" __super__;");
		} else {
			cgen.current.app("struct _").app(classDecl.getSuperRef().getUnderName()).app("Class __super__;");
		}

		/* Now write all virtual functions prototypes in the class struct */
		for (FunctionDecl decl : classDecl.getFunctions()) {
			if(classDecl.getSuperRef() != null) {
				FunctionDecl superDecl = classDecl.getSuperRef().getFunction(decl.getName(), decl.getSuffix(), null);
				if(superDecl != null && !decl.getName().equals("init")) continue;
			}
			
			cgen.current.nl();
			writeFunctionDeclPointer(decl, true, cgen);
			cgen.current.app(';');
		}
		
		for (VariableDecl decl : classDecl.getVariables()) {
			if (!decl.isStatic() || decl.isExtern())
				continue;
			cgen.current.nl();
			decl.accept(cgen);
			cgen.current.app(';');
		}
		
		cgen.current.closeBlock().app(';').nl().nl();
	}

	public static void writeObjectStruct(ClassDecl classDecl, CGenerator cgen)
			throws IOException {
		cgen.current.nl().app("struct _").app(classDecl.getUnderName()).openSpacedBlock();

		if (classDecl.isClassClass()) {
			cgen.current.app(CLASS_NAME).app(" *class;");
		} else if (!classDecl.isObjectClass()) {
			cgen.current.app("struct _").app(classDecl.getSuperRef().getUnderName()).app(
					" __super__;");
		}

		for (VariableDecl decl : classDecl.getVariables()) {
			if (decl.isStatic())
				continue;
			cgen.current.nl();
			if(VariableDeclWriter.write(decl, cgen)) {
				cgen.current.app(';');
			}
		}

		cgen.current.closeBlock().app(';').nl().nl();
	}

	public static void writeStructTypedef(String structName, CGenerator cgen) throws IOException {
		cgen.current.nl().app("struct _").app(structName).app(";");
		cgen.current.nl().app("typedef struct _").app(structName).app(" ").app(
				structName).app(";");
	}

	public static void writeDesignatedInit(FunctionDecl parentDecl, FunctionDecl realDecl, boolean impl, CGenerator cgen)
			throws IOException {

			if(realDecl != null && realDecl.isAbstract()) return;
			
			cgen.current.nl().app('.').app(parentDecl.getSuffixedName()).app(" = ");
			if(realDecl != null) {
				cgen.current.app("(");
				writeFunctionDeclPointer(parentDecl, false, cgen);
				cgen.current.app(") ");
			}
			
			cgen.current.app(realDecl != null ? realDecl.getFullName() : parentDecl.getFullName());
			if(impl) cgen.current.app("_impl");
			cgen.current.app(',');
			
	}

}
