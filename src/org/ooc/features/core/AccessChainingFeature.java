package org.ooc.features.core;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.CoupleFeature;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.functions.MemberFunctionCall;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.Parenthesis;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;

/**
 * Resolve constructs like:
 * <code>object.field.method()</code>
 * 
 * @author Amos Wenger
 */
public class AccessChainingFeature extends CoupleFeature<VariableAccess, Dot> {
	
	/**
	 * Default constructor
	 */
	public AccessChainingFeature() {
		super(VariableAccess.class, Dot.class);
	}

	@Override
	protected void applyImpl(AssemblyManager manager, VariableAccess access, Dot dot) {
		
		Type type = access.getType();
		type.assemble(manager);
		
		System.out.println("Trying to assemble access "+access.toString().trim()+".something");
		
		SyntaxNode next = dot.getNext();
		
		if(type.clazz == null || !type.isFlat()) {
			if(type.metaClazz != null) {
				handleMeta(manager, access, dot, type, next);
			}
			System.out.println("Type "+type.name+" without class or flat, abandoning.");
			return;
		}
		
		handleRegular(manager, access, dot, type, next);
		
	}

	private void handleRegular(AssemblyManager manager, VariableAccess access,
			Dot dot, Type type, SyntaxNode next) {
		
		ClassDef def = type.clazz.getClassDef();
		if(next instanceof Name) {
			String name = ((Name) next).content;
			System.out.println("Got name, now is "+access.toString()+"."+name);
			Function func = def.getImplementation(manager.getContext(), name, new TypedArgumentList(next.location));
			if(func != null) {
				SyntaxNode nextNext = next.getNext();
				if(nextNext instanceof Parenthesis) {
					Parenthesis paren = (Parenthesis) nextNext;
					if(!paren.nodes.isEmpty()) {
						manager.errAndFail("[AccessChainingFeature] Trying to call function "+func.getSimpleName()+" with arguments "
								+paren+", but this function has no arguments!", paren);
					}
					paren.drop();
				}
				next.drop();
				FunctionCall call = new FunctionCall(next.location, name);
				call.impl = func;
				handleMemberFunctionCall(manager, dot, access, def, call);
			} else {
				System.out.println("It's a member \\o/");
				handleMemberAccess(manager, dot, access, def);
			}
		} else if(next instanceof FunctionCall) {
			handleMemberFunctionCall(manager, dot, access, def, (FunctionCall) next);
		}
		
	}

	private void handleMeta(AssemblyManager manager, VariableAccess access, Dot dot,
			Type type, SyntaxNode next) {

		ClassDef def = type.metaClazz.getClassDef();
		
		if(next instanceof Name) {
			Name name = (Name) next;
			if(def.hasMetaClassField(name.content)) {
				
				name.drop();
				dot.drop();
				access.replaceWith(manager, new MemberAccess(access.location, access,
						new Variable(Type.OBJECT, name.content)));
				System.out.println("Handled metaclazz field successfully!");
				
			} else {
				
				manager.errAndFail("No such field '"+name.content+"' in a metaclass.", );
				
			}
		}
		
	}

	private void handleMemberAccess(AssemblyManager manager, Dot dot, VariableAccess access, ClassDef def) {
		
		Name name = (Name) dot.getNext();
		Variable member = def.getMember(manager.getContext(), name.content);
		if(member != null) {
			
			name.drop();
			dot.drop();
			access.replaceWith(manager, new MemberAccess(access.location, access, member));
			
		} else {
			
			// Queue the access, as it's the node that will trigger this feature.
			manager.queue(access, "No member "+name.content+" found in class "+def.clazz.fullName);
			
		}
		
	}
	
	private void handleMemberFunctionCall(AssemblyManager manager, Dot dot,
			VariableAccess access, ClassDef def, FunctionCall call) {
		
		Function func = def.getImplementation(manager.getContext(),
				call.name, new TypedArgumentList(call));
		
		if(func != null) {
			
			if(call.getParent() != null) {
				call.drop();
			}
			dot.drop();
			access.replaceWith(manager, new MemberFunctionCall(access.location, call.name, call.nodes, access));
			
		} else {
			
			// Queue the access, as it's the node that will trigger this feature.
			manager.queue(access, "No function "+call.name+call.getArgTypesRepr()+" found in "+def.clazz.fullName);
			
		}
		
	}

}
