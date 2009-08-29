package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.numeric.IntLiteral;
import org.ooc.nodes.others.Parenthesis;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A for loop
 *
 * @author Amos Wenger
 */
public class For extends Parenthesis {

	/** The name of the variable we're iterating into */
    public Variable index;
    
    /** The lower bound of the for loop */
    public SyntaxNode lower;
    
    /** The upper bound of the for loop */
    public SyntaxNode upper;
    
    /** The step of the for loop */
    public SyntaxNode step;
    
    /** Should do reverse iterations? */
    public boolean reverse;

    /**
     * Default constructor
     * @param location
     */
    public For(FileLocation location) {
        super(location);
        index = null;
        lower = new IntLiteral(location, 0);
        upper = new IntLiteral(location, 0);
        step = new IntLiteral(location, 1);
        reverse = false;
    }
    
    
    @Override
	protected boolean isIndented() {
    	return true;
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	
    	writeWhitespace(a, -1);
        if(index == null) {
            a.append("for(");
            for(SyntaxNode node: nodes) {
                node.writeToCSource(a);
            }
            a.append(")");
        } else {
        	a.append("for(int ");
			a.append(index.getName());
			a.append(" = ");
			if(reverse) {
				a.append("(");
				upper.writeToCSource(a);
				a.append(") - 1");
			} else {
				lower.writeToCSource(a);
			}
            a.append("; ");
            a.append(index.getName());
            if(reverse) {
            	a.append(" >= ");
            	lower.writeToCSource(a);
            } else {
            	a.append(" < ");
            	upper.writeToCSource(a);
            }
            a.append("; ");
            a.append(index.getName());
            if(reverse) {
            	a.append(" -= ");
            } else {
            	a.append(" += ");
            }
			step.writeToCSource(a);
            a.append(") ");
        }
        
    }

	
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
    	assembleAll(manager);
    	if(lower != null) {
    		manager.queue(lower, "For queuing its lower");
    	}
    	if(upper != null) {
    		manager.queue(upper, "For queuing its upper");
    	}
    	if(step != null) {
    		manager.queue(step, "For queuing its step");
    	}
    	
    }

}
