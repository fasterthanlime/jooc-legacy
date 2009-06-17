package org.ooc.nodes.keywords;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.PotentiallyStatic;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The static keywords differenciates between static (class) fields, and instance
 * (object) fields. 
 * 
 * @author Amos Wenger
 */
public class StaticKeyword extends Keyword {

	/**
	 * Default constructor
	 * @param location
	 */
	public StaticKeyword(FileLocation location) {
		super(location);
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		SyntaxNode next = getNearestNextNotTyped(Keyword.class);
		if(next instanceof PotentiallyStatic) {
        	
            final PotentiallyStatic candidate = (PotentiallyStatic) next;
            candidate.setStatic(true);
            drop();
            
        } else {
        	
            manager.errAndFail("Expected variable declaration/function definition" +
            		"after 'static' keyword, not a "+next.getClass().getSimpleName()+", wtf?", this);
            
        }
		
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {

		writeWhitespace(a);
		a.append("static");

	}
	
	@Override
	protected boolean isSpaced() {
		return true;
	}

}
