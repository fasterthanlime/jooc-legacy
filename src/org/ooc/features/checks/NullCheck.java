package org.ooc.features.checks;

import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.SingleFeature;
import org.ooc.nodes.control.If;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.functions.MemberFunctionCall;
import org.ooc.nodes.libs.Include;
import org.ooc.nodes.libs.Include.IncludePosition;
import org.ooc.nodes.libs.Include.IncludeType;
import org.ooc.nodes.numeric.IntLiteral;
import org.ooc.nodes.operators.EqualityTest;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.RawCode;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.nodes.text.StringLiteral;
import org.ubi.FileLocation;

/**
 * Check that an object is non-null before calling a member function on it 
 * 
 * @author Amos Wenger
 */
public class NullCheck extends SingleFeature<MemberFunctionCall> {

	private static List<MemberFunctionCall> callsDone = new ArrayList<MemberFunctionCall>();
	
	/**
	 * Default constructor
	 */
	public NullCheck() {
		super(MemberFunctionCall.class);
	}

	@Override
	protected void applyImpl(AssemblyManager manager, MemberFunctionCall call) {
		
		if(!callsDone.contains(call)) {
			
			callsDone.add(call);
			
			FileLocation location = call.location;
			SyntaxNodeList nullCheck = new SyntaxNodeList(location);
			
				If ifNode = new If(location);
					ifNode.add(call.getAccess());
					ifNode.add(new EqualityTest(location));
					ifNode.add(new RawCode(location, "null"));
				nullCheck.add(ifNode);
				
				Scope ifBody = new Scope(location);
					FunctionCall printfCall = new FunctionCall(location, "printf");
						printfCall.add(new StringLiteral(location, location+" NullPointerException: trying to call member function on null object '"+call.getAccess().toString()+"'!\n"));
					ifBody.add(printfCall);
					ifBody.add(new LineSeparator(location));
					FunctionCall exitCall = new FunctionCall(location, "exit");
						exitCall.add(new IntLiteral(location, 1));
					ifBody.add(exitCall);
					ifBody.add(new LineSeparator(location));
				nullCheck.add(ifBody);
			
			if(!call.addToPrevLineOrScope(nullCheck)) {
				manager.errAndFail("Couldn't find a Scope in which to insert a null check. Wtf?", call);
			}
			SyntaxNode next = nullCheck.getNext();

			if(next instanceof VariableDecl) {
				VariableDecl decl = (VariableDecl) next;
				if(call.access.equals(decl.variable)) {
					decl.swap(nullCheck);
					call.lock();
				}
			}
			nullCheck.flatten();
				
			if(callsDone.isEmpty()) {
				manager.getContext().add(new Include(location, IncludeType.PATHBOUND, IncludePosition.HEADER, "stdlib.h"));
			}
		}
		
	}
	
}
