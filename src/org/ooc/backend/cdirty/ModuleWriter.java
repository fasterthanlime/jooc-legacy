package org.ooc.backend.cdirty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.middle.UseDef;

public class ModuleWriter {
	
	public static void write(Module module, CGenerator cgen) throws IOException {
		
		/** Classify imports */
		List<Import> tightImports = new ArrayList<Import>();
		List<Import> looseImports = new ArrayList<Import>();
		looseImports.addAll(module.getImports()); // imports are loose by default
		
		for(TypeDecl selfDecl: module.getTypes().values()) {
			for(Import imp: module.getImports()) {
				if(selfDecl.getSuperRef() != null 
						&& selfDecl.getSuperRef().getModule().equals(imp.getModule())) {
					// tighten imports of modules which contain classes we extend
					if(looseImports.remove(imp)) {
						tightImports.add(imp);
					}
				} else if(imp.getModule().getFullName().startsWith("lang.")) {
					// tighten imports of core modules
					if(looseImports.remove(imp)) {
						tightImports.add(imp);
					}
				} else {
					for(VariableDecl member: selfDecl.getVariables()) {
						Declaration ref = member.getType().getRef();
						if(!(ref instanceof CoverDecl)) continue;
						CoverDecl coverDecl = (CoverDecl) ref;
						if(coverDecl.getFromType() != null) continue;
						if(coverDecl.getModule() != imp.getModule()) continue;
						// uses compound cover, tightening!
						if(looseImports.remove(imp)) {
							tightImports.add(imp);
						}
					}
				}
			}
		}
		
		/** Write the -fwd.h file */
		cgen.current = cgen.fw;
		cgen.current.app("/* ");
		cgen.current.app(module.getFullName());
		cgen.current.app(" header file, generated with ooc */");
		cgen.current.nl();
		
		String hFwdName = "__" + module.getUnderName() + "__fwd__";
		cgen.current.app("#ifndef ");
		cgen.current.app(hFwdName);
		cgen.current.nl();
		cgen.current.app("#define ");
		cgen.current.app(hFwdName);
		cgen.current.nl();
		cgen.current.nl();
		
		for(Use use: module.getUses()) {
			UseDef useDef = use.getUseDef();
			for(String include: useDef.getIncludes()) {
				cgen.current.nl().app("#include <").app(include).app(">");
			}
		}
		
		for(Include include: module.getIncludes()) {
			IncludeWriter.write(include, cgen);
		}
		
		for(TypeDecl node: module.getTypes().values()) {
			if(node instanceof ClassDecl) {
				ClassDecl classDecl = (ClassDecl) node;
				String className = classDecl.getUnderName();
				ClassDeclWriter.writeStructTypedef(className, cgen);
				ClassDeclWriter.writeStructTypedef(className+"Class", cgen);
			} else if(node instanceof CoverDecl) {
				CoverDeclWriter.writeTypedef((CoverDecl) node, cgen);
			}
		}
		cgen.current.nl();
		
		cgen.current = cgen.fw;
		
		// foward-include .c-level imports (which doesn't contain types we extend)
		for(Import imp: looseImports) {
			String include = imp.getModule().getOutPath('/');
			cgen.current.nl().app("#include <").app(include).app("-fwd.h>");
		}
		
		for(Import imp: tightImports) {
			String include = imp.getModule().getOutPath('/');
			cgen.current.nl().app("#include <").app(include).app("-fwd.h>");
		}
		
		/** Write the .h file */
		cgen.current = cgen.hw;
		cgen.current.app("/* ");
		cgen.current.app(module.getFullName());
		cgen.current.app(" header file, generated with ooc */");
		cgen.current.nl();
		
		String hName = "__" + module.getUnderName() + "__";
		cgen.current.app("#ifndef ");
		cgen.current.app(hName);
		cgen.current.nl();
		cgen.current.app("#define ");
		cgen.current.app(hName);
		cgen.current.nl();
		cgen.current.nl();

		cgen.current.nl();
		cgen.current.app("#include \"");
		cgen.current.app(module.getSimpleName());
		cgen.current.app("-fwd.h\"");
		cgen.current.nl();
		
		// include .h-level imports (which contains types we extend)
		for(Import imp: tightImports) {
			String include = imp.getModule().getOutPath('/');
			cgen.current.nl().app("#include <").app(include).app(".h>");
		}
		cgen.current.nl();
		
		/** Write the .c file */
		cgen.current = cgen.cw;
		cgen.current.app("/* ");
		cgen.current.app(module.getFullName());
		cgen.current.app(" source file, generated with ooc */");
		cgen.current.nl();
		
		cgen.current.app("#include \"");
		cgen.current.app(module.getSimpleName());
		cgen.current.app(".h\"");
		cgen.current.nl();
		
		for(Import imp: looseImports) {
			String include = imp.getModule().getOutPath('/');
			cgen.current.app("#include <").app(include).app(".h>").nl();
		}
		
		for(TypeDecl node:  module.getTypes().values()) {
			node.accept(cgen);
		}
		
		for(Node node: module.getBody()) {
			if(node instanceof Line) {
				Line line = (Line) node;
				Node inner = line.getStatement();
				if(!(inner instanceof VariableDecl)) {
					node.accept(cgen);
				}
			} else {
				node.accept(cgen);
			}
		}
		module.getOps().accept(cgen);
		module.getLoadFunc().accept(cgen);
		
		ModuleWriter.writeLoadFunc(cgen);
		if(module.isMain() && cgen.params.defaultMain) writeDefaultMain(cgen);
		
		/** Finish the .h file (global variables/functions) **/
		cgen.current = cgen.hw;
		
		cgen.current.nl();
		for(Node node: module.getBody()) {
			if(node instanceof Line) {
				Line line = (Line) node;
				Node inner = line.getStatement();
				if(inner instanceof VariableDecl) {
					node.accept(cgen);
				}
			}
		}
		
		cgen.current.nl().nl().app("#endif // ").app(hName).nl().nl();
		
		/** Finish the -fwd.h file */
		
		cgen.current = cgen.fw;
		cgen.current.nl().nl().app("#endif // ").app(hFwdName).nl().nl();
		
	}
	
	private static void writeDefaultMain(CGenerator cgen) throws IOException {
		
		if(!cgen.params.link) return;
		
		boolean got = false;
		for(Node node: cgen.module.getBody()) {
			if(!(node instanceof FunctionDecl)) continue;
			FunctionDecl decl = (FunctionDecl) node;
			if(decl.isEntryPoint(cgen.params)) {
				got = true;
			}
		}
		if(!got) {
			cgen.current.nl().app("int ").app(cgen.params.entryPoint).app("()").openBlock();
			if(cgen.params.enableGC) {
				cgen.current.nl().app("GC_INIT();");
			}
			cgen.current.nl().app(cgen.module.getLoadFunc().getName()).app("();");
			cgen.current.closeBlock().nl().nl();
		}
		
	}

	public static void writeLoadFunc(CGenerator cgen)
			throws IOException {

		cgen.current = cgen.hw;
		cgen.current.nl();
		Type.getVoid().accept(cgen);
		cgen.current.app(' ').app(cgen.module.getLoadFunc().getName()).app("();");

		cgen.current = cgen.cw;
		cgen.current.nl();
		Type.getVoid().accept(cgen);
		cgen.current.app(' ').app(cgen.module.getLoadFunc().getName()).app("()").openBlock();

		cgen.current.nl().app("static ").app("bool __done__ = false;").nl().app("if (!__done__)").openBlock();
		cgen.current.nl().app("__done__ = true;");

		for(TypeDecl typeDecl: cgen.module.getTypes().values()) {
			if (typeDecl instanceof ClassDecl) {
				ClassDecl classDecl = (ClassDecl) typeDecl;
				cgen.current.nl().app(classDecl.getName()).app("_").app(
						classDecl.getFunction(ClassDecl.LOAD_FUNC_NAME, "", null).getName()).app("();");
			}
		}
		for (Import imp : cgen.module.getImports()) {
			cgen.current.nl().app(imp.getModule().getLoadFunc().getName()).app("();");
		}
		for (Node node : cgen.module.getLoadFunc().getBody()) {
			node.accept(cgen);
		}

		cgen.current.closeBlock().closeSpacedBlock();

	}

}
