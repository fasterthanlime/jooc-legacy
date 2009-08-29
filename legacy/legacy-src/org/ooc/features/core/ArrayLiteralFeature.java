package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.TrioFeature;
import org.ooc.nodes.array.ArrayDecl;
import org.ooc.nodes.array.ArrayLiteral;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.operators.Assignment;

/**
 * Recognize array literals 
 * 
 * @author Amos Wenger
 */
public class ArrayLiteralFeature extends TrioFeature<ArrayDecl, Assignment, Scope> {

	/**
	 * Default constructor
	 */
	public ArrayLiteralFeature() {
		super(ArrayDecl.class, Assignment.class, Scope.class);
	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, ArrayDecl decl,
			Assignment assignment, Scope scope) {

		scope.replaceWith(manager, new ArrayLiteral(scope.location, decl, scope));
		
	}
	
}
