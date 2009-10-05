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
	
	public static final String LANG_PREFIX = "lang__";
	public static final String CLASS_NAME = LANG_PREFIX+"Class";
	
	public static void write(ClassDecl classDecl, CGenerator cgen) throws IOException {
		
		cgen.current = cgen.hw;
		
		writeObjectStruct(classDecl, cgen);
		writeClassStruct(classDecl, cgen);
		writeMemberFuncPrototypes(classDecl, cgen);
		
		/* Now implementations */
		cgen.current = cgen.cw;
		cgen.current.nl();
		
		writeInstanceImplFuncs(classDecl, cgen);
		writeClassGettingFunction(classDecl, cgen);
		writeInstanceVirtualFuncs(classDecl, cgen);
		writeStaticFuncs(classDecl, cgen);
		
		cgen.current.nl();
		
	}

	public static void writeStaticFuncs(ClassDecl classDecl, CGenerator cgen) throws IOException {

		for (FunctionDecl decl : classDecl.getFunctions()) {

			if (!decl.isStatic() || (decl.isExtern() && decl.getExternName().length() > 0))
				continue;

			cgen.current.nl();
			FunctionDeclWriter.writeFuncPrototype(decl, cgen);
			cgen.current.openBlock();
			decl.getBody().accept(cgen);
			cgen.current.closeSpacedBlock();

		}
	}

	public static void writeInstanceVirtualFuncs(ClassDecl classDecl,
			CGenerator cgen) throws IOException {

		for (FunctionDecl decl : classDecl.getFunctions()) {

			if (decl.isStatic() || decl.isFinal())
				continue;

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
					"Class *)((lang__Object *)this)->class)->");
			decl.writeSuffixedName(cgen.current);

			FunctionDeclWriter.writeFuncArgs(decl, ArgsWriteMode.NAMES_ONLY, baseClass, cgen);
			cgen.current.app(";").closeSpacedBlock();

		}
	}

	public static void writeInstanceImplFuncs(ClassDecl classDecl,
			CGenerator cgen) throws IOException {

		// Non-static (ie. instance) functions
		for (FunctionDecl decl : classDecl.getFunctions()) {
			if (decl.isStatic() || decl.isAbstract() || (decl.isExtern() && decl.getExternName().length() > 0))
				continue;
			
			FunctionDeclWriter.writeFuncPrototype(decl, cgen, decl.isFinal() ? null : "_impl");
			cgen.current.openBlock();
			if(decl.getName().equals(ClassDecl.DEFAULTS_FUNC_NAME) && classDecl.getSuperName().length() > 0) {
				cgen.current.nl().app(classDecl.getSuperName()).app("_")
					.app(ClassDecl.DEFAULTS_FUNC_NAME).app("_impl((")
					.app(classDecl.getSuperRef().getUnderName()).app(" *) this);");
			}
			decl.getBody().accept(cgen);
			cgen.current.closeSpacedBlock();
		}

	}

	public static void writeClassGettingFunction(ClassDecl classDecl,
			CGenerator cgen) throws IOException {

		cgen.current.app(CLASS_NAME).app(" *").app(classDecl.getName()).app("_class()")
				.openSpacedBlock();
		if (classDecl.getSuperName().length() > 0)
			cgen.current.app("static ").app(LANG_PREFIX).app("Bool __done__ = false;").nl();
		cgen.current.app("static ").app(classDecl.getUnderName()).app(
				"Class class = ");
		
		writeClassStructInitializers(classDecl, classDecl, new HashSet<FunctionDecl>(), cgen);
		
		cgen.current.app(';');
		cgen.current.nl().app(CLASS_NAME).app(" *classPtr = (").app(CLASS_NAME).app(" *) &class;");
		if (classDecl.getSuperName().length() > 0) {
			cgen.current.nl().app("if(!__done__)").openBlock().nl().app(
					"__done__ = true;").nl().app("classPtr->super = ").app(
					classDecl.getSuperName()).app("_class();").closeBlock();
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
			//if (parentDecl.isStatic()) continue;
			
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

		cgen.current.nl().app(CLASS_NAME).app(" *").app(classDecl.getName()).app("_class();").nl();
		for (FunctionDecl decl : classDecl.getFunctions()) {
			
			if(decl.isExtern() && decl.getExternName().length() > 0) {
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
			//if (decl.isStatic())
			//	continue;
			
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
			decl.accept(cgen);
			cgen.current.app(';');
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
