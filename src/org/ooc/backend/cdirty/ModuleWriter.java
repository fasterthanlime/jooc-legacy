package org.ooc.backend.cdirty;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
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

		for(Include include: module.getIncludes()) {
			IncludeWriter.write(include, cgen);
		}
		for(Use use: module.getUses()) {
			UseDef useDef = use.getUseDef();
			for(String include: useDef.getIncludes()) {
				cgen.current.nl().app("#include <").app(include).app(">");
			}
		}
		
		for(String key: module.getTypes().keySet()) {
			for(TypeDecl node : module.getTypes().getAll(key)) {
				if(node instanceof ClassDecl) {
					ClassDecl classDecl = (ClassDecl) node;
					String className = classDecl.getUnderName();
					ClassDeclWriter.writeStructTypedef(className, cgen);
					ClassDeclWriter.writeStructTypedef(className+"Class", cgen);
				} else if(node instanceof CoverDecl) {
					CoverDeclWriter.writeTypedef((CoverDecl) node, cgen);
				}
			}
		}
		cgen.current.nl();
		
		cgen.current.nl();
		for(Import imp: module.getImports()) {
			String include = imp.getModule().getFullName().replace('.', File.separatorChar);
			cgen.current.app("#include <").app(include).app(".h>").nl();
		}
		for(Node node: module.getBody()) {
			if(node instanceof Line) {
				Line line = (Line) node;
				Node inner = line.getStatement();
				if(inner instanceof VariableDecl) {
					node.accept(cgen);
				}
			}
		}
		
		cgen.current = cgen.cw;
		cgen.current.app("/* ");
		cgen.current.app(module.getFullName());
		cgen.current.app(" source file, generated with ooc */");
		cgen.current.nl();
		
		cgen.current.app("#include \"");
		cgen.current.app(module.getSimpleName());
		cgen.current.app(".h\"");
		cgen.current.nl();
		
		for(String key: module.getTypes().keySet()) {
			for(TypeDecl node : module.getTypes().getAll(key)) {
				node.accept(cgen);
			}
		}
		//module.acceptChildren(cgen);
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
		module.getTypes().accept(cgen);
		module.getOps().accept(cgen);
		module.getLoadFunc().accept(cgen);
		
		ModuleWriter.writeLoadFunc(cgen);
		if(module.isMain()) writeDefaultMain(cgen);
		
		cgen.current = cgen.hw;
		cgen.current.nl().nl().app("#endif // ").app(hName).nl().nl();
		
	}
	
	private static void writeDefaultMain(CGenerator cgen) throws IOException {
		
		boolean got = false;
		for(Node node: cgen.module.getBody()) {
			if(!(node instanceof FunctionDecl)) continue;
			FunctionDecl decl = (FunctionDecl) node;
			if(decl.isEntryPoint()) {
				got = true;
			}
		}
		if(!got) {
			cgen.current.nl().app("int main()").openBlock();
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

		cgen.current.nl().app("static bool __done__ = false;").nl().app("if (!__done__)").openBlock();
		cgen.current.nl().app("__done__ = true;");

		for (String key : cgen.module.getTypes().keySet()) {
			for (TypeDecl typeDecl: cgen.module.getTypes().getAll(key)) {
				if (typeDecl instanceof ClassDecl) {
					ClassDecl classDecl = (ClassDecl) typeDecl;
					cgen.current.nl().app(classDecl.getName()).app("_").app(
							classDecl.getFunction(ClassDecl.LOAD_FUNC_NAME, "", null).getName()).app("();");
				}
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
