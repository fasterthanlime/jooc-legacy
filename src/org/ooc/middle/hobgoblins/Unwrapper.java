package org.ooc.middle.hobgoblins;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashSet;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Opportunist;
import org.ooc.middle.walkers.SketchyNosy;

/**
 * The {@link Unwrapper} transforms this kind of statement <code>
 * printf("The answer is %d\n", Int i = 42);
 * </code>
 * 
 * Into this kind of statement: <code>
 * {
 *   Int i = 42;
 *   printf("The answer is %d\n", i);
 * }
 * </code>
 * 
 * Of course, the {@link Unwrapper} does nothing by itself, it's all specified
 * in node classes which implement the {@link MustBeUnwrapped} interface.
 * 
 * @author Amos Wenger
 */
public class Unwrapper implements Hobgoblin {

	protected static final int MAX = 1024;
	boolean running;

	@Override
	public void process(Module module, BuildParams params) throws IOException {

		resolveSuper(module, new HashSet<Module>());

		SketchyNosy nosy = new SketchyNosy(new Opportunist<Node>() {
			@Override
			public boolean take(Node node, NodeList<Node> stack)
					throws IOException {
				if (node instanceof MustBeUnwrapped) {
					MustBeUnwrapped must = (MustBeUnwrapped) node;
					if (must.unwrap(stack))
						running = true;
				}
				return true;
			}
		});

		int count = 0;
		running = true;
		while (running) {
			if (count > MAX) {
				throw new Error("Unwrapper going round in circles! More than "
						+ MAX + " runs, abandoning...");
			}
			running = false;
			nosy.visit(module); // changes running to true if there was damage
			count++;
		}

	}

	/*
	private void addBuiltins(Module module) {
		decls.add(new BuiltinType("void"));
		decls.add(new BuiltinType("short"));
		decls.add(new BuiltinType("unsigned short"));
		decls.add(new BuiltinType("int"));		
		decls.add(new BuiltinType("unsigned int"));
		decls.add(new BuiltinType("long"));
		decls.add(new BuiltinType("unsigned long"));
		decls.add(new BuiltinType("long long"));
		decls.add(new BuiltinType("unsigned long long"));
		decls.add(new BuiltinType("long double"));
		decls.add(new BuiltinType("unsigned long double"));
		decls.add(new BuiltinType("float"));
		decls.add(new BuiltinType("double"));
		decls.add(new BuiltinType("char"));
		decls.add(new BuiltinType("unsigned char"));
		decls.add(new BuiltinType("signed char"));
		
		decls.add(new BuiltinType("bool"));
		
		decls.add(new BuiltinType("int8_t"));
		decls.add(new BuiltinType("int16_t"));
		decls.add(new BuiltinType("int32_t"));
		
		decls.add(new BuiltinType("uint8_t"));
		decls.add(new BuiltinType("uint16_t"));
		decls.add(new BuiltinType("uint32_t"));
		
		decls.add(new BuiltinType("size_t"));		
		decls.add(new BuiltinType("time_t"));
	}
	*/

	private void resolveSuper(Module module, HashSet<Module> hashSet) throws OocCompilationError,
			EOFException {
		
		hashSet.add(module);
		
		MultiMap<String, TypeDecl> types = module.getTypes();
		for (String key : types.keySet()) {
			for (TypeDecl decl : types.getAll(key)) {
				String superName = decl.getSuperName();
				if (superName.isEmpty())
					continue;
				TypeDecl superRef = module.getType(superName);
				decl.setSuperRef(superRef);
				if (superRef == null) {
					throw new OocCompilationError(decl, module,
							"Couldn't resolve parent type " + superName
									+ " of type " + decl.getName());
				}
			}
		}
		
		for(Import imp: module.getImports()) {
			if(!hashSet.contains(imp.getModule())) {
				resolveSuper(imp.getModule(), hashSet);
			}
		}
	}

}
