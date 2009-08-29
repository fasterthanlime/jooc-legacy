package org.ooc.nodes.keywords;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.PotentiallyConst;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The const keyword prevents modification of the variable declared as such.  
 * 
 * @author Amos Wenger
 */
public class ConstKeyword extends Keyword {

	/**
	 * Default constructor
	 * @param location
	 */
	public ConstKeyword(FileLocation location) {
		super(location);
	}
	
    
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
    	SyntaxNode next = getNearestNextNotTyped(Keyword.class);
		if(next instanceof PotentiallyConst) {
			
			final PotentiallyConst candidate = (PotentiallyConst) next;
            candidate.setConst(true);
            drop();
			
		} else {
        	
            manager.errAndFail("Expected a variable declaration after 'const' keyword," +
            		" not a "+next.getClass().getSimpleName()+", wtf?", this);
            
        }
    	
    }

	
	public void writeToCSource(Appendable a) throws IOException {
		writeWhitespace(a);
		a.append("const");
	}
	
	
	@Override
	protected boolean isSpaced() {
		return true;
	}
	
}
