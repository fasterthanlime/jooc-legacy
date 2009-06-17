package org.ooc.nodes.numeric;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.operators.Minus;
import org.ooc.nodes.others.Literal;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 *
 * @author Amos Wenger
 */
public abstract class NumberLiteral extends Literal {

    protected NumberLiteral(FileLocation location) {
        super(location);
    }

    @Override
    protected void assembleImpl(AssemblyManager manager) {
    	
    	if(getParent() == null) {
    		return;
    	}
    	
        SyntaxNode prev = getParent().getPrev(this);
        if(prev != null && prev instanceof Minus) {
        	getParent().remove(prev);
            negate();
        }
        
        lock();
        
    }

    protected abstract void negate();

}
