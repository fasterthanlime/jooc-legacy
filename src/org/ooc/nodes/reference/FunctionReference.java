package org.ooc.nodes.reference;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.clazz.StaticMemberAccess;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A reference to a function, e.g.
 * <code>
 * func blah {
 *   printf("blih\n");
 * }
 * Func pointer = @blah; // Reference
 * pointer();
 * </code>
 * 
 * @author Amos Wenger
 */
public class FunctionReference extends SyntaxNode implements Typed {

	/** The type of a function */
	public static Type type = Type.baseType("Func");
	protected final String name;
	protected Function impl;
	
	/**
	 * Default constructor
	 * @param location
	 */
	public FunctionReference(FileLocation location, String name) {
		super(location);
		this.name = name;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {

		Scope scope = getParent().getNearest(Scope.class);
		SourceContext context = manager.getContext();
		
		if(scope == null) {
			manager.queue(this, "Null scope.. Hierarchy is "+getHierarchyRepr());
			return;
		}
		
		ClassDef def = getParent().getNearest(ClassDef.class);
		if(def != null) {
			Variable member = def.getMember(context, name);
			if(member != null) {
				if(member.isStatic) {
					replaceWith(manager, new StaticMemberAccess(location, def.clazz, member));
				} else {
					replaceWith(manager, new MemberAccess(location, def.clazz.getThis(), member));
				}
				return;
			}
		}
		
		Variable var = scope.getVariable(name);
		if(var != null) {
			replaceWith(manager, new VariableAccess(location, var));
			return;
		}
		
		impl = scope.getImplementation(context, name);
		if(impl == null) {
			manager.queue(this, "No implementation found for '"+name+"'");
			return;
		}
		
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {

		writeWhitespace(a);
		a.append("((Func) ");
		a.append(impl.getMangledName());
		a.append(")");
		
	}
	
	@Override
	protected boolean isSpaced() {
	
		return true;
		
	}

	/**
	 * @return of the function we're referring to
	 */
	public String getName() {

		return name;
		
	}

	/**
	 * @return the implementation of this function
	 */
	public Function getImpl() {

		return impl;
		
	}
	
}
