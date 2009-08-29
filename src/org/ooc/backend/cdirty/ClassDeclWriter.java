package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.model.*;

public class ClassDeclWriter {
	
	public static void write(ClassDecl classDecl, CGenerator cgen) throws IOException {
		
		cgen.current = cgen.hw;
		
		String className = classDecl.getName();
		
		writeObjectStruct(classDecl, className, cgen);
		writeClassStruct(classDecl, className, cgen);
		writeMemberFuncPrototypes(classDecl, className, cgen);
		
		/* Now implementations */
		cgen.current = cgen.cw;
		cgen.current.nl();
		
		writeInitializeClassFunc(classDecl, className, cgen);
		writeDestroyFunc(classDecl, className, cgen);
		writeInstanceImplFuncs(classDecl, className, cgen);
		writeClassGettingFunction(classDecl, cgen);
		writeInstanceVirtualFuncs(classDecl, className, cgen);
		writeStaticFuncs(classDecl, className, cgen);
		
		cgen.current.nl();
		
	}

	public static void writeMemberFuncPrototype(String className,
			FunctionDecl decl, CGenerator cgen) throws IOException {

		TypeWriter.writeSpaced(decl.getReturnType(), cgen);
		cgen.current.app(className).app('_').app(decl.getSuffixedName());
		writeFuncArgs(decl, cgen);

	}

	public static void writeFuncArgs(FunctionDecl decl, CGenerator cgen) throws IOException {
		writeFuncArgs(decl, false, cgen);
	}

	public static void writeFuncArgs(FunctionDecl decl, boolean skipFirst,
			CGenerator cgen) throws IOException {

		cgen.current.app('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		if (iter.hasNext()) { // of course, no point of doing all cgen if we
								// have no arguments
			if (skipFirst)
				iter.next(); // especially that.
			while (iter.hasNext()) {
				iter.next().accept(cgen);
				if (iter.hasNext())
					cgen.current.app(", ");
			}
		}
		cgen.current.app(')');

	}

	public static void writeTypelessFuncArgs(FunctionDecl decl, CGenerator cgen)
			throws IOException {

		cgen.current.app('(');
		Iterator<Argument> iter = decl.getArguments().iterator();
		while (iter.hasNext()) {
			cgen.current.app(iter.next().getName());
			if (iter.hasNext())
				cgen.current.app(", ");
		}
		cgen.current.app(')');

	}

	public static void writeStaticFuncs(ClassDecl classDecl, String className,
			CGenerator cgen) throws IOException {

		for (FunctionDecl decl : classDecl.getFunctions()) {

			if (!decl.isStatic())
				continue;

			cgen.current.nl();
			writeMemberFuncPrototype(className, decl, cgen);
			cgen.current.openBlock();
			decl.getBody().accept(cgen);
			cgen.current.closeSpacedBlock();

		}
	}

	public static void writeInstanceVirtualFuncs(ClassDecl classDecl,
			String className, CGenerator cgen) throws IOException {

		for (FunctionDecl decl : classDecl.getFunctions()) {

			if (decl.isStatic() || decl.isFinal())
				continue;

			cgen.current.nl();
			writeMemberFuncPrototype(className, decl, cgen);
			cgen.current.openSpacedBlock();

			if (!decl.getReturnType().isVoid())
				cgen.current.app("return ");
			cgen.current.app("((").app(className).app(
					"Class *)((Object *)this)->class)->");
			decl.writeSuffixedName(cgen.current);

			writeTypelessFuncArgs(decl, cgen);
			cgen.current.app(";").closeSpacedBlock();

		}
	}

	public static void writeBuiltinClassFuncName(String className,
			String returnType, String name, CGenerator cgen) throws IOException {
		cgen.current.nl().app("static ").app(returnType).app(' ')
				.app(className).app('_').app(name).app('(').app(className).app(
						" *this)");
	}

	public static void writeInitializeClassFunc(ClassDecl classDecl,
			String className, CGenerator cgen) throws IOException {

		writeBuiltinClassFuncName(className, "void", "initialize", cgen);
		cgen.current.openBlock();
		if (!classDecl.getSuperName().isEmpty()) {
			cgen.current.nl();
			cgen.current.app(classDecl.getSuperName());
			cgen.current.app("_class()->initialize((Object *) this);");
		}
		for (Line line : classDecl.getInitializeFunc().getBody())
			line.accept(cgen);
		cgen.current.closeSpacedBlock();

		cgen.current.nl().app("void ").app(className).app("_load()")
				.openBlock();
		cgen.current.nl().app("static bool __done__ = false;").nl().app(
				"if (!__done__)").openBlock();
		cgen.current.nl().app("__done__ = true;");
		for (Line line : classDecl.getLoadFunc().getBody()) {
			line.accept(cgen);
		}
		cgen.current.closeBlock().closeSpacedBlock();

	}

	public static void writeDestroyFunc(ClassDecl classDecl, String className,
			CGenerator cgen) throws IOException {

		cgen.current.nl();
		writeBuiltinClassFuncName(className, "void", "destroy", cgen);
		cgen.current.openBlock().openSpacedBlock();
		cgen.current
				.app("const Class *super = ((Object *) this)->class->super;");
		cgen.current.nl().app("if(super) super->destroy((Object *) this);");
		cgen.current.closeSpacedBlock().closeSpacedBlock();
	}

	public static void writeInstanceImplFuncs(ClassDecl classDecl,
			String className, CGenerator cgen) throws IOException {

		// Non-static (ie. instance) functions
		for (FunctionDecl decl : classDecl.getFunctions()) {
			if (decl.isStatic() || decl.isAbstract())
				continue;

			cgen.current.nl();
			if (!decl.isFinal())
				cgen.current.app("static ");
			TypeWriter.writeSpaced(decl.getReturnType(), cgen);
			decl.writeFullName(cgen.current);
			if (!decl.isFinal())
				cgen.current.app("_impl");

			// if is constuctor, don't write the first arg
			writeFuncArgs(decl, decl.isConstructor(), cgen);

			cgen.current.openBlock();

			/* Special case: constructor */
			if (decl.isConstructor()) {
				cgen.current.nl().app(className).app(" *this = (").app(
						className).app(" *) Class_newInstance((Class *)").app(
						className).app("_class());");
				cgen.current.nl().app(className).app("_construct");
				if (!decl.getSuffix().isEmpty())
					cgen.current.app('_').app(decl.getSuffix());
				writeTypelessFuncArgs(decl, cgen);
				cgen.current.app(";").nl().app("return this;");
			} else {
				decl.getBody().accept(cgen);
			}
			cgen.current.closeSpacedBlock();

			// Special case: constructor, now write the corresponding construct
			if (decl.isConstructor()) {
				cgen.current.app("void ").app(className).app("_construct");
				if (!decl.getSuffix().isEmpty())
					cgen.current.app('_').app(decl.getSuffix());
				writeFuncArgs(decl, cgen);
				cgen.current.openBlock();
				for (Line line : decl.getBody())
					line.accept(cgen);
				cgen.current.closeSpacedBlock();
			}
		}

	}

	public static void writeClassGettingFunction(ClassDecl classDecl,
			CGenerator cgen) throws IOException {

		cgen.current.app("Class *").app(classDecl.getName()).app("_class()")
				.openSpacedBlock();
		cgen.current.app("static bool __done__ = false;").nl();
		cgen.current.app("static ").app(classDecl.getName()).app(
				"Class class = ");
		writeFuncPointers(classDecl, classDecl, cgen);
		cgen.current.app(';');
		cgen.current.nl().app("Class *classPtr = (Class *) &class;");
		if (!classDecl.getSuperName().isEmpty()) {
			cgen.current.nl().app("if(!__done__)").openBlock().nl().app(
					"__done__ = true;").nl().app("classPtr->super = ").app(
					classDecl.getSuperName()).app("_class();").closeBlock();
		}

		cgen.current.nl().app("return classPtr;").closeSpacedBlock();
	}

	public static void writeFuncPointers(ClassDecl writerClass,
			ClassDecl coreClass, CGenerator cgen) throws IOException {

		cgen.current.openBlock();

		if (!writerClass.isRootClass() && !writerClass.getSuperName().isEmpty()) {

			writeFuncPointers(writerClass.getSuperRef(), coreClass, cgen);

		} else {

			cgen.current.openBlock();
			cgen.current.nl().app(".size = ").app("sizeof(").app(
					coreClass.getName()).app("),");
			cgen.current.nl().app(".name = ").app('"').app(coreClass.getName())
					.app("\",");
			writeDesignatedInit("initialize", "(void (*)(Object *))"
					+ coreClass.getName() + "_initialize", cgen);
			writeDesignatedInit("destroy", "(void (*)(Object *))"
					+ coreClass.getName() + "_destroy", cgen);

			cgen.current.closeBlock().app(',');

		}

		for (FunctionDecl decl : writerClass.getFunctions()) {
			if (decl.isStatic() || decl.isConstructor())
				continue;

			if (decl.isFinal() || decl.isAbstract()) {
				writeDesignatedInit(decl.getSuffixedName(), writerClass.getName() + "_"
						+ decl.getSuffixedName(), cgen);
			} else {
				writeDesignatedInit(decl.getSuffixedName(), writerClass.getName() + "_"
						+ decl.getSuffixedName() + "_impl", cgen);
			}

		}

		cgen.current.closeBlock();
		if (coreClass != writerClass)
			cgen.current.app(',');
	}

	public static void writeMemberFuncPrototypes(ClassDecl classDecl,
			String className, CGenerator cgen) throws IOException {

		cgen.current.nl().app("Class *").app(className).app("_class();").nl();
		for (FunctionDecl decl : classDecl.getFunctions()) {
			cgen.current.nl();
			TypeWriter.writeSpaced(decl.getReturnType(), cgen);
			decl.writeFullName(cgen.current);
			writeFuncArgs(decl, decl.isConstructor(), cgen);
			cgen.current.app(';');

			if (decl.getName().equals("new")) {
				cgen.current.nl().app("void ").app(className).app("_construct");
				if (!decl.getSuffix().isEmpty())
					cgen.current.app('_').app(decl.getSuffix());
				writeFuncArgs(decl, cgen);
				cgen.current.app(';');
			}
		}
		cgen.current.nl();
	}

	public static void writeFuncPointer(FunctionDecl decl, CGenerator cgen)
			throws IOException {
		decl.getReturnType().accept(cgen);
		cgen.current.app(" (*");
		decl.writeSuffixedName(cgen.current);
		cgen.current.app(")(");
		int numArgs = decl.getArguments().size() - 1;
		Node[] args = decl.getArguments().getNodes();
		for (int i = 0; i <= numArgs; i++) {
			args[i].accept(cgen);
			if (i < numArgs)
				cgen.current.app(", ");
		}
		cgen.current.app(')');
	}

	public static void writeClassStruct(ClassDecl classDecl, String className, CGenerator cgen)
			throws IOException {

		cgen.current.nl().app("struct _").app(className).app("Class")
				.openSpacedBlock();
		if (classDecl.isRootClass()) {
			cgen.current.app("struct _Class __super__;");
		} else {
			cgen.current.app("struct _").app(classDecl.getSuperName()).app(
					"Class __super__;");
		}

		/* Now write all virtual functions prototypes in the class struct */
		for (FunctionDecl decl : classDecl.getFunctions()) {
			if (decl.isStatic() || decl.isConstructor())
				continue;
			cgen.current.nl();
			writeFuncPointer(decl, cgen);
			cgen.current.app(';');
		}
		cgen.current.closeBlock().app(';').nl().nl();
	}

	public static void writeObjectStruct(ClassDecl classDecl, String className, CGenerator cgen)
			throws IOException {
		cgen.current.nl().app("struct _").app(className).openSpacedBlock();

		if (classDecl.isClassClass()) {
			cgen.current.app("Class *class;");
		} else if (!classDecl.isObjectClass()) {
			cgen.current.app("struct _").app(classDecl.getSuperName()).app(
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

		for (VariableDecl decl : classDecl.getVariables()) {
			if (!decl.isStatic())
				continue;
			cgen.current.nl();
			decl.accept(cgen);
			cgen.current.app(';');
		}
	}

	public static void writeStructTypedef(String structName, CGenerator cgen) throws IOException {
		cgen.current.nl().app("struct _").app(structName).app(";");
		cgen.current.nl().app("typedef struct _").app(structName).app(" ").app(
				structName).app(";");
	}

	public static void writeDesignatedInit(String contract, String implementation, CGenerator cgen)
			throws IOException {
		cgen.current.nl().app('.').app(contract).app(" = ").app(implementation).app(
				',');
	}

}
