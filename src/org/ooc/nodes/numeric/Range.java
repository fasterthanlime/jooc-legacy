package org.ooc.nodes.numeric;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.nodes.control.For;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.keywords.ReverseKeyword;
import org.ooc.nodes.others.Comma;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ubi.FileLocation;

/**
 * A range, e.g. 0..100, represents a common data type, which is useful, e.g.
 * for iterating over a portion of an array, or to slice a string, etc.
 * While it has no corresponding type yet in the implementation, it's useable
 * in foreaches for now, e.g.
 * <code>
 * for(int i: 0..100) {
 *   printf("%d ", i);
 * }
 * </code>
 *
 * @author Amos Wenger
 */
public class Range extends SyntaxNodeList {

	/** Lower bound of the range, e.g. 0 for 0..100 */
    public SyntaxNode lower;
    
    /** Upper bound of the range, e.g. 100 for 0..100 */
    public SyntaxNode upper;
    
    /** Is is set up for reverse iteration? */
    public boolean reverse;

    /**
     * Default constructor
     * @param location
     */
    public Range(FileLocation location) {
    	
        super(location);
        reverse = false;
        
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	
    	throw new CompilationFailedError(location, "fixme: A Range hasn't been assembled properly. Was it outside a Foreach?");
        
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
    	//System.out.println(this.getClass().getSimpleName()+location+" assembling... (btw, virgin = "+isVirgin()+")");
	
        lower = getParent().getPrev(this);
        upper = getParent().getNext(this);
        
        if(lower == null) {
        	//System.out.println("can't find a prev for the range at "+location);
            manager.err("No lower bound for Range", this);
            return;
        }
        
        if(!(lower instanceof Typed) || manager.isDirty(lower)) {
        	//System.out.println("lower which is "+lower.getClass().getSimpleName()+" not assembled yet, returning false");
        	manager.queue(lower, "Lower of a Range and still not finished assembling");
        	manager.queue(this, "Lower (a "+lower.getDescription()+") not finished assembling.");
        	return;
        }
        
        if(upper == null) {
        	//System.out.println("can't find a next for the range at "+location);
            manager.err("No upper bound for Range", this);
            return;
        }
        
        if(!(upper instanceof Typed) || manager.isDirty(upper)) {
        	//System.out.println("upper which is "+upper.getClass().getSimpleName()+" not assembled yet, returning false");
        	manager.queue(upper, "Upper of a Range and still not finished assembling (or is it ? "+upper.isLocked()+") and next is "+upper.getNext());
        	//manager.queueRecursive(parent, "Upper of a Range and still not finished assembling");
        	manager.queue(this, "Upper (a "+upper.getDescription()+") not finished assembling.");
        	return;
        }
        
        SyntaxNode next = upper.getNext();
		if(getParent() instanceof For && next != null) {
			boolean ok = false;
			For forNode = (For) getParent();
        	if(next instanceof Comma) {
        		SyntaxNode nextNext = next.getNext();
        		if(nextNext instanceof Typed) {
        			Typed typed = (Typed) nextNext;
        			if(typed.getType().equals(IntLiteral.type)) {
        				forNode.step = nextNext;
        				nextNext.drop();
        				next.drop();
        				ok = true;
        			}
        		}
        	}
        	if(!ok) {
        		manager.queue(this, "We're in a for and upper still has next");
        		return;
        	}
        }
		
		SyntaxNode prev = lower.getPrev();
		if(prev instanceof ReverseKeyword) {
			reverse = true;
			prev.drop();
		}
        
        //System.out.println("Range "+location+", neither prev nor next are dirty, moving.. Lower is a "+lower.getDescription()+", upper is a "+upper.getDescription());
        
        lower.moveTo(this);
        upper.moveTo(this);
        
        lock();
        
    }

}
