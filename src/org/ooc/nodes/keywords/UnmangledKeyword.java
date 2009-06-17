package org.ooc.nodes.keywords;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.PotentiallyUnmangled;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * Support of the 'unmangled' keyword. This keyword, when used before a function
 * declaration, prevents the mangling of the name of this function at compile time.
 * So instead of 'my_package_MyClass_myFunction', the generated name would be
 * only 'myFunction'. This is useful when you want to expose a part of your
 * API (Application Programming Interface) to pure C
 * 
 * @author Amos Wenger
 */
public class UnmangledKeyword extends Keyword {

	/**
	 * Default constructor
	 * @param location
	 */
	public UnmangledKeyword(FileLocation location) {
		super(location);
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {
		// Fiddledididooo.
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		SyntaxNode next = getNearestNextNotTyped(Keyword.class);
        if(next instanceof PotentiallyUnmangled) {
        	
            final PotentiallyUnmangled candidate = (PotentiallyUnmangled) next;
            candidate.setUnmangled(true);
            
        } else {
        	
        	if(next == null) {
        	
        		manager.err("Nothing found after 'unmangled' keyword! What do you want to make unmangled ?", this);
        		
        	} else {
        		
        		manager.err("Unexpected 'unmangled' keyword before a "+next.getClass().getSimpleName()+", whadayamean, man ?", this);
        		
        	}
            
        }
		
	}
	
}
