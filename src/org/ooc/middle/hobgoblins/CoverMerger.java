package org.ooc.middle.hobgoblins;

import java.io.IOException;

import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

public class CoverMerger implements Hobgoblin {

	@Override
	public void process(final Module module, BuildParams params) throws IOException {
		
		Nosy.get(CoverDecl.class, new Opportunist<CoverDecl>() {
			@Override
			public boolean take(CoverDecl node, NodeList<Node> stack)
					throws IOException {
				
				for(Import imp: module.getImports()) {
					if(searchFor(imp.getModule(), node)) break;
				}
				return true;
				
			}
		}).visit(module);
		
	}

	boolean searchFor(final Module module, final CoverDecl child) throws IOException {
		
		Nosy.get(CoverDecl.class, new Opportunist<CoverDecl> () {
			@Override
			public boolean take(CoverDecl node, NodeList<Node> stack) throws IOException {
				if(!node.getName().equals(child.getName())) return true;
				child.absorb(node);
				return false;
			}
			
		}).visit(module);
		return child.isAddon();
		
	}

}
