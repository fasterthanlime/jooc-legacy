package org.ooc.middle.hobgoblins;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashSet;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NamespaceDecl;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
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

	public boolean process(Module module, BuildParams params) throws IOException {

		resolveSuper(module, new HashSet<Module>());

		SketchyNosy nosy = new SketchyNosy(new Opportunist<Node>() {
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
		
		return false;

	}

	private void resolveSuper(Module module, HashSet<Module> hashSet) throws OocCompilationError,
			EOFException {
		
		hashSet.add(module);
		
		for(TypeDecl decl: module.getTypes().values()) {
			Type superType = decl.getSuperType();
			if(superType == null) continue;
			TypeDecl superRef = null;
			if(superType.getNamespace() == null) {
				// no namespace? global namespace.
				superRef = module.getType(superType.getName());
			} else {
				// look for this namespace.
				NamespaceDecl ns = module.getNamespace(superType.getNamespace());
				if(ns == null) {
					throw new OocCompilationError(decl, module, superType.getNamespace() + ": This Namespace Does Not Exist.");
				}
				superRef = ns.resolveType(superType.getName());
			}
			if (superRef == null) {
				throw new OocCompilationError(decl, module,
						"Couldn't resolve parent type " + superType.getName()
								+ " of type " + decl.getName());
			}
			superType.setRef(superRef);
		}
		
		for(Import imp: module.getAllImports()) {
			if(!hashSet.contains(imp.getModule())) {
				resolveSuper(imp.getModule(), hashSet);
			}
		}
	}

}
