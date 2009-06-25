package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.LinearNode;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.numeric.Range;
import org.ooc.nodes.types.Cast;
import org.ooc.nodes.types.Type;
import org.ooc.nodes.types.TypeReference;
import org.ubi.FileLocation;

/**
 * A pair of parenthesis with children nodes inside, e.g. "(...)"
 *
 * @author Amos Wenger
 */
public class Parenthesis extends SyntaxNodeList implements Typed, LinearNode {

    private int nameCheck = 0;

    /**
     * Default constructor
     * @param location
     */
	public Parenthesis(FileLocation location) {
        super(location);
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
        a.append("(");
        for(SyntaxNode node: nodes) {
            node.writeToCSource(a);
        }
        a.append(")");
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
    	if(!assembleAll(manager) || manager.isDirtyRecursive(this)) { // Dirty already ?
    		manager.queue(this, "Recursively-dirty children");
    		return;
    	}
    	
        if(nodes.size() == 1) {
        	
        	SyntaxNode node = nodes.get(0);
            SyntaxNode next = getParent().getNext(this);
            
            if(next instanceof Name && nameCheck < 5) { // Oh the hack =D
            	
            	nameCheck++;
            	manager.queue(next, "Parenthesis hoping for its only-child name to assemble more...");
                manager.queue(this, "Parenthesis hoping for its only-child name to assemble more... (nameCheck == "+nameCheck+"). Name's content = "+((Name) next).content);
                return;
                
            } else if(node instanceof TypeReference && next instanceof VariableAccess) {
            	
            	if(manager.isDirty(next)) {
            		manager.queue(this, "next VariableAccess is Dirty");
            		return;
            	}
                next.drop();
                Cast cast = new Cast(location, (TypeReference) node, ((VariableAccess) next));
				replaceWith(manager, cast);
                return;
                
            } else if(node instanceof VariableAccess || node instanceof Range) {
            	
                // We still have only one child, it's stoopid for it to be in Parenthesis
                replaceWith(manager, node);
                manager.queue(node, "Parenthesis replaced with a "+node.getClass().getSimpleName()+", assembling it.");
                return;
                
            }
            
            lock();
            
        }
        
    }

	
	public Type getType() {
		
		if(nodes.isEmpty()) {
			return Type.UNKNOWN;
		}
		for(int j = nodes.size() - 1; j >= 0; j--) {
			SyntaxNode node = nodes.get(j);
			if(node instanceof Typed) {
				Type type = ((Typed) node).getType();
				if(type != null) {
					return type;
				}
			}
		}
		return Type.UNKNOWN;
		
	}

}