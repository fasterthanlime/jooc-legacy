package org.ooc.features.core;

import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.control.For;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.functions.MemberFunctionCall;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.others.Block;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * Assign to a variable before calling a member function on something. 
 * 
 * @author Amos Wenger
 */
public class MemberCallUnwrapFeature extends SingleFeature<MemberFunctionCall> {

	private static List<MemberFunctionCall> callsDone = new ArrayList<MemberFunctionCall>();
	
	/**
	 * Default constructor
	 */
	public MemberCallUnwrapFeature() {
		super(MemberFunctionCall.class);
	}

	@Override
	protected void applyImpl(AssemblyManager manager, MemberFunctionCall call) {
		
		if(!callsDone.contains(call) && (call.access.getClass() != VariableAccess.class
				&& call.access.getClass() != MemberAccess.class) || call.getParent() instanceof For) {
			
			FileLocation location = call.location;
			
			Block tempVarCode = new Block(location);
			Variable tempVar = call.getNearest(Scope.class).generateTempVariable(call.access.getType(), "tmp");
			tempVarCode.add(new VariableDecl(location, tempVar));
			tempVarCode.add(new Assignment(location));
			tempVarCode.add(call.access);
			tempVarCode.add(new LineSeparator(location));
			
			if(!call.addToPrevLineOrScope(tempVarCode)) {
				manager.errAndFail("Couldn't find a place to insert intermediate variable declaration before member function call. Wtf?", call.access);
			}
			
			SyntaxNode next = tempVarCode.getNext();
			SyntaxNode prev = tempVarCode.getPrev();
			System.out.println(tempVarCode.location+" TempVar's next is a "+(next == null ? "null" : next.getClass().getSimpleName())
					+", prev is a "+(prev == null ? "null" : prev.getClass().getSimpleName()));
			
			tempVarCode.flatten();
			
			// now update access
			call.access = new VariableAccess(location, tempVar);
			
			callsDone.add(call);
			
		}
		
	}
		
}
