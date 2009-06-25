package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.CoupleFeature;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.others.Block;
import org.ooc.nodes.others.Initialization;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableDecl;

/**
 * Sometimes it's useful to initialize member variables in the class definition,
 * not in any constructors. It's especially handy for static member variables, ie.
 * not having to put static blocks everywhere.
 * 
 * @author Amos Wenger
 */
public class InitializationFeature extends CoupleFeature<VariableDecl, Assignment> {

	/**
	 * Default constructor
	 */
	public InitializationFeature() {
		
		super(VariableDecl.class, Assignment.class);

	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, VariableDecl decl,
			Assignment ass) {

		if(!(ass.getParent() instanceof ClassDef)) {
			return; // We only deal with members. This is a private club.
		}
		
		ClassDef def = (ClassDef) ass.getParent();
		
		if(decl.variable.isStatic) {

			SyntaxNode value = ass.getNext();
			Initialization init = new Initialization(ass.location, value);
			value.replaceWith(manager, init);
			ass.drop();
			decl.setInitialization(init);

		} else {
			
			Block block = new Block(ass.location);
			block.add(new MemberAccess(ass.location, def.clazz.getThis() , decl.variable));
			
			SyntaxNode current = ass.getNext();
			ass.moveTo(block);
			
			while(current != null) {
				
				if(current instanceof LineSeparator) {
					break;
				}
				
				// we need to getNext before we moveTo, or we null out the parent.
				SyntaxNode toAdd = current;
				current = ass.getNext();
				toAdd.moveTo(block);
				
			}
			
			block.add(new LineSeparator(ass.location));
			
			def.addInstanceInitialization(block);
		
		}
		
	}
	
}
