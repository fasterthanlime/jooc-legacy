package org.ooc.features.checks;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.types.Type;

/**
 * Check the type of assignments, e.g.:
 * <code>
 * String s = 30; // ERROR: can't assign an int to a String.
 * </code>
 * 
 * @author Amos Wenger
 */
public class AssignmentTypeCheck extends SingleFeature<Assignment> {

	/**
	 * Default constructor
	 */
	public AssignmentTypeCheck() {
		super(Assignment.class);
	}

	
	@Override
	protected void applyImpl(AssemblyManager manager, Assignment assignment) {

		SyntaxNode prev = assignment.getPrev();
		if(!(prev instanceof Typed)) {
			return;
		}
		
		SyntaxNode next = assignment.getNext();
		if(!(next instanceof Typed)) {
			return;
		}
		
		if(manager.isDirty(prev) || manager.isDirty(next)) {
			manager.queue(assignment, "Waiting on prev/next");
			return;
		}
		
		Type leftType = ((Typed) prev).getType();
		Type rightType = ((Typed) next).getType();
		
		if(leftType.isUnknown() || rightType.isUnknown()) {
			manager.queue(prev, "prev of assignment, should check for type");
			manager.queue(next, "next of assignment, should check for type");
			manager.queue(assignment, "Left type ("+prev.getClass().getSimpleName()+"#"+prev.hash+" "+leftType.toString(manager)
					+") and/or right type ("+next.getClass().getSimpleName()+"#"+next.hash+" "+rightType.toString(manager)+") unknown");
			return;
		}
		
		rightType.checkCast(leftType, manager);
		
	}

}
