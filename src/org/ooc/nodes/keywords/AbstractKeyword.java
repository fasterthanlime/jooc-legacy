package org.ooc.nodes.keywords;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.interfaces.PotentiallyAbstract;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * Implemented by everything that can be abstract, such as.. function declarations !
 *
 * @author Amos Wenger
 */
public class AbstractKeyword extends Keyword {

	/**
	 * Default constructor
	 * @param location
	 */
    public AbstractKeyword(final FileLocation location) {
        super(location);
    }

    
    @Override
	protected void assembleImpl(final AssemblyManager manager) {
        
        SyntaxNode next = getNearestNextNotTyped(Keyword.class);
        if(next instanceof PotentiallyAbstract) {
        	
            final PotentiallyAbstract candidate = (PotentiallyAbstract) next;
            candidate.setAbstract(true);
            if(candidate instanceof FunctionDef) {
                final SyntaxNode nextNext = next.getNext();
                if(!(nextNext instanceof LineSeparator)) {
                    manager.err("Missing semi-colon after abstract function definition", next);
                }
            }
            
        } else {
        	
            manager.err("Unexpected abstract keyword. abstract should be before a function or a class. Why are you trying to make a "
            		+next.getClass().getSimpleName()
            		+" abstract, man?", this);
            
        }

    }

    public void writeToCSource(final Appendable appendable) throws IOException {
        // What should I say? I don't know. I think I have forgotten my text. Anyone?
    }

}
