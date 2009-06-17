package org.ooc.nodes.types;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ubi.FileLocation;

/**
 * Typedefs to function pointers have a waaaaaaaaay weird syntax, e.g.
 * <code>
 * typedef returnType (*functionName)(arg1, arg2, arg3);
 * </code>
 * vs simple typedefs, e.g.:
 * <code>
 * typedef type aliasName;
 * </code>
 * 
 * @author Amos Wenger
 */
public class TypeDefFunctionPointer extends TypeDef {

	/** The arguments of the function pointed by this typedef */
	private final String args;

	/**
	 * Default constructor
	 * @param location
	 * @param type
	 * @param name
	 * @param args
	 */
	public TypeDefFunctionPointer(FileLocation location, Type type,
			String name, String args) {
		
		super(location, type, name);
		
		//System.err.println("Got new TypeDefFunctionPointer named "+name);
		
		this.args = args;
		this.type.isResolved = true;
		
	}
	
	@Override
	public void writeToCHeader(Appendable a) throws IOException {
		
		writeWhitespace(a);
		a.append("typedef ");
		type.writeToCSource(a);
		if(type.getPointerLevel() == 0) {
            a.append(' ');
        }
		a.append("(*");
		a.append(name);
		a.append(")(");
		a.append(args);
		a.append(");\n");
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		// Huh. I'm afraid we're not handling type checking on function pointers right now..
		lock();
		
	}

}
