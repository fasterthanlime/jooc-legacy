package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.CoupleFeature;
import org.ooc.nodes.others.Comma;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.Separator;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.structures.Variable;

/**
 * Recognize multiple variable declarations, e.g.
 * <code>
 * Int a = 1, b = 2, c = 3;
 * </code>
 * 
 * @author Amos Wenger
 */
public class MultipleVarDeclFeature extends CoupleFeature<Comma, Name> {

	/**
	 * Default constructor
	 */
	public MultipleVarDeclFeature() {
		super(Comma.class, Name.class);
	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, Comma comma, Name name) {

		VariableDecl prevDecl = null;
		SyntaxNode current = comma.getPrev();
		while(current != null) {
			if(current instanceof Separator) {
				return;
			}
			if(current instanceof VariableDecl) {
				prevDecl = (VariableDecl) current;
				break;
			}
			current = current.getPrev();
		}
		
		if(prevDecl == null) {
			return;
		}
		
		LineSeparator separator = new LineSeparator(comma.location);
		comma.replaceWith(manager, separator);
		Variable var = new Variable(prevDecl.getType(), name.content);
		var.isConst = prevDecl.variable.isConst;
		var.isStatic = prevDecl.variable.isStatic;
		VariableDecl newDecl = new VariableDecl(name.location, var);
		separator.addAfter(newDecl);
		name.drop();
		
		newDecl.assembleForce(manager);
		
	}

	
	
}
