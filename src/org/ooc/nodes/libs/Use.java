package org.ooc.nodes.libs;

import java.io.IOException;

import org.ooc.compiler.libraries.Library;
import org.ooc.compiler.libraries.LibraryManager;
import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.RootNode;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The 'use' keyword is a magic mechanism in ooc, which allows the use of
 * libraries in the easiest possible way =) E.g. "use glut;" in a program
 * includes the needed headers, depending on the platform you're building
 * on, and also provides the necessary linking information to the backend
 * (e.g. static/dynamic libraries).
 * All the magic happens in {@link LibraryManager}
 * @see LibraryManager
 * @see Library
 * 
 * @author Amos Wenger
 */
public class Use extends SyntaxNode {

	/** The name of the used library */
	public final String name;

	/**
	 * Default constructor
	 * @param location
	 * @param name
	 */
	public Use(final FileLocation location, final String name) {
		super(location);
		this.name = name;
	}

	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		if(!(getParent() instanceof RootNode)) {
			manager.errAndFail(this.getClass().getSimpleName()+" in a "+getParent().getClass().getSimpleName()+". Should be at root level", this);
		}
		
		SourceContext.libManager.resolveIncludes(this);
		
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {
		// Nothing to write
	}
	
	

}
