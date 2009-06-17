package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.Parenthesis;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * A for loop
 *
 * @author Amos Wenger
 */
public class For extends Parenthesis {

	/** The name of the variable we're iterating into */
    public String index;
    
    /** The lower bound of the for loop */
    public String lower;
    
    /** The upper bound of the for loop */
    public String upper;
    
    /** The step of the for loop */
    public String step;

    /**
     * Default constructor
     * @param location
     */
    public For(FileLocation location) {
        super(location);
        this.index = "";
    }
    
    @Override
    protected boolean isIndented() {
    	return true;
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	
    	writeWhitespace(a, -1);
        if(index.isEmpty()) {
            a.append("for(");
            for(SyntaxNode node: nodes) {
                node.writeToCSource(a);
            }
            a.append(")");
        } else {
        	String indexStr = index.toString().trim();
        	String lowerStr = lower.toString().trim();
        	String upperStr = upper.toString().trim();
        	String stepStr = step.toString().trim();
        	
            a.append("for(int ");
			a.append(indexStr);
            a.append(" = ");
			a.append(lowerStr);
            a.append("; ");
            a.append(indexStr);
            a.append(" < ");
			a.append(upperStr);
            a.append("; ");
            a.append(indexStr);
            a.append(" += ");
			a.append(stepStr);
            a.append(") ");
        }
        
    }

	@Override
    protected void assembleImpl(AssemblyManager manager) {
    	
    	assembleAll(manager);
    	
    }

}
