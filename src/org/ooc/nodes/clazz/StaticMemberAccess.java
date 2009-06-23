package org.ooc.nodes.clazz;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.structures.Clazz;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * Access to a static member, ie. "MyClass.myStaticField" 
 * @see MemberAccess
 * @see VariableAccess
 * 
 * @author Amos Wenger
 */
public class StaticMemberAccess extends VariableAccess {

	private Clazz clazz;

	/**
	 * Default constructor
	 * @param location
	 * @param clazz
	 * @param variable
	 */
	public StaticMemberAccess(FileLocation location, Clazz clazz, Variable variable) {
		
		super(location, variable);
		this.clazz = clazz;
		if(clazz == null) {
			throw new CompilationFailedError(location, "Heeeeeeeelp! initialized StaticMemberAccess with a null clazz !");
		}
		
		if(!variable.isStatic) {
			throw new CompilationFailedError(location, "Attempt to access non-static member "
					+variable.getName()+" as a static member. Did you forget to declare it static?");
		}
		
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		writeWhitespace(a);
		a.append(variable.getName(clazz));
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		SourceContext context = manager.getContext();
		while(clazz.hasSuper() && clazz.getZuper(context).getClassDef().getMember(context, variable.getName()) != null) {
			clazz = clazz.getZuper(context);
		}
		
	}
	
}
