package org.ooc.nodes.array;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.LinearNode;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A subscript is something between square brackets, ie. [3] or ["good!"]
 *
 * @author Amos Wenger
 */
public class Subscript extends SyntaxNodeList implements LinearNode {

	/**
	 * Default constructor
	 * @param location
	 */
    public Subscript(FileLocation location) {
        super(location);
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
        a.append("[");
        super.writeToCSource(a);
        a.append("]");
    }

    @Override
    protected void assembleImpl(AssemblyManager manager) {
    	
    	if(!assembleAll(manager)) {
    		return;
    	}
    	
        SyntaxNode prev = getParent().getPrev(this);
        if(prev instanceof VariableAccess) {
        	
            VariableAccess access = (VariableAccess) prev;
            if(access.getType().getPointerLevel() > 0 || access.getType().getArrayLevel() > 0) {
                prev.drop();
                ArrayAccess arrayAccess = new ArrayAccess(location, access, this);
				replaceWith(manager, arrayAccess);
                return;
            }
            
        } else if(prev instanceof Name) {

            Name namePrev = (Name) prev;
            SyntaxNode next = getParent().getNext(this);
            if(next instanceof Name) {
                Name nameNext = (Name) next;
                replaceWith(manager, new ArrayDecl(location, new Variable(new Type(location, this, namePrev.content, 0), nameNext.content), this));
                prev.drop();
                next.drop();
                return;
            }
            
        }

    }

}
