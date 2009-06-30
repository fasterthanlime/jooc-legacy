package org.ooc.nodes.others;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.control.For;
import org.ooc.nodes.control.ForEach;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.numeric.Range;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A ':'. Mostly used in foreaches, and for the "condition ? action1 : action2" construct. 
 *
 * @author Amos Wenger
 */
public class Colon extends RawCode {

	/**
	 * Default constructor
	 */
    public Colon(FileLocation location) {
        super(location, ": ");
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
        if(getParent() instanceof For) {
        	SyntaxNode prev = getParent().getPrev(this);
        	SyntaxNode next = getParent().getNext(this);
        	
        	if(!prev.isLocked()) {
        		manager.queue(prev, "Hoping for prev ["+prev.getClass().getSimpleName()+"] at "+prev.location+" to assemble more");
        		manager.queue(this, "Hoping for prev ["+prev.getClass().getSimpleName()+"] at "+prev.location+" to assemble more");
        		return;
        	}
        	if(!next.isLocked()) {
        		manager.queue(next, "Hoping for next ["+next.getClass().getSimpleName()+"] at "+next.location+" to assemble more");
        		manager.queue(this, "Hoping for next ["+next.getClass().getSimpleName()+"] at "+next.location+" to assemble more");
        		return;
        	}
        	
            if(!(prev instanceof VariableDecl)) {
                manager.queue(prev, "Expecting a variable declaration, not a "+prev.getClass().getSimpleName()
                		+" which looks like "+prev);
                return;
            }
            if(!(next instanceof Range || (next instanceof Typed && next.getNext() == null))) {
                manager.queue(this, "Readjusting position of Colon in the queue: must be after its children."); // Just so that it is at the end.
                return;
            }
            if(next instanceof VariableAccess) {
            	if(next.getNext() == null) {
            		VariableAccess access = (VariableAccess) next;
					Type type = access.getType();
            		if(type.clazz == null) {
            			manager.errAndFail("Trying to foreach over a non-class type. Did you forget to import a cover?", next);
            		}
            		if(type.clazz.getUnmangledFunctionsRecursive(manager.getContext(), "iterator").isEmpty()) {
            			manager.queue(this, "Trying to foreach over the variable '"+access.toString()+"' of type '"+type.name+"' which doesn't have an iterator method.");
            			return;
            		}
					ForEach foreach = new ForEach(location, (VariableDecl) prev, access);
                    getParent().replaceWith(manager, foreach);
            	} else {
            		manager.queue(this, "Got VariableAccess, but not the last of the parenthesis.");
            	}
                return;
            } else if(next instanceof Typed) {
            	if(next.getNext() == null) {
            		next.drop();
            		
            		Scope scope = getParent().getNearest(Scope.class);
            		Variable tmpVar = scope.generateTempVariable(new Type(location, null, "Iterable"), "collection");
            		getParent().addBefore(new VariableDecl(location, tmpVar));
            		getParent().addBefore(new Assignment(location));
            		getParent().addBefore(next);
            		getParent().addBefore(new LineSeparator(location));
            		manager.queueVirgins(getParent(), "Unwrapped a collection for a foreach");
            		
            		ForEach foreach = new ForEach(location, (VariableDecl) prev, new VariableAccess(location, tmpVar));
                    getParent().replaceWith(manager, foreach);
            	} else {
            		manager.queue(this, "Got Typed, but not the last of the parenthesis.");
            	}
                return;
            } else if(next instanceof Range) {
                Range range = (Range) next;
                For forr = (For) getParent();
                if(range.lower instanceof Typed && range.upper instanceof Typed /* && range.lower.isAssembled() && range.upper.isAssembled()*/) {
                	Typed lowerTyped = (Typed) range.lower;
                	Typed upperTyped = (Typed) range.upper;
                	if(true || lowerTyped.getType().name.equals("int") && upperTyped.getType().name.equals("int")) { // That sucks.
	                    forr.index = ((VariableDecl) prev).variable;
	                    forr.lower = range.lower;
	                    forr.upper = range.upper;
	                    forr.reverse = range.reverse;
                	} else {
                		manager.queue(range, "Range after Colon still dirty, queuing.");
                		manager.queue(this, "Range after Colon still dirty, queuing the Colon in prevision.");
                		//manager.errAndFail("Unknown lower type "+lowerTyped.getType()+" / upper type "+upperTyped.getType(), this);
                	}
                } else {
                	manager.queue(range, "Range after Colon unknown type, queuing.");
                	manager.queue(this, "Range lower ("+range.lower.getClass().getSimpleName()
                			+") / upper ("+range.upper.getClass().getSimpleName()
                			+") after Colon unknown, queuing the Colon in prevision.");
                	/*manager.errAndFail("Unknown lower type "+range.lower.getClass().getSimpleName()
                			+" / upper type "+range.upper.getClass().getSimpleName(), this);*/
                }
                return;
            }
			manager.err("Unexpected type node after colon", this);
			return;
        }

    }

}
