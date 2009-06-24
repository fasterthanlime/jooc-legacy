package org.ooc.nodes.types;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.array.ArrayDecl;
import org.ooc.nodes.array.Subscript;
import org.ooc.nodes.operators.Star;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * Represents a reference to a type, e.g. "int", or "float[]"
 * 
 * @author Amos Wenger
 */
public class TypeReference extends SyntaxNode {

	private Type type;
	
	/**
	 * Default constructor
	 * @param location
	 * @param type
	 */
	public TypeReference(FileLocation location, Type type) {
		super(location);
		this.type = type;
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {
		type.writeToCSource(a);
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		while(true) {
	    	SyntaxNode next = getNext();
	    	
			if(next instanceof Star) {

				type = type.derivePointerLevel(type.getPointerLevel() + 1);
	    		next.drop();
	    		
	    	} else if(next instanceof Subscript) {
	    		
	    		Subscript sub = (Subscript) next;
	    		if(sub.nodes.isEmpty()) {
	    			type = type.deriveArrayLevel(1);
	    		} else if(sub.getNext() instanceof Name) {
	    			Name name = (Name) sub.getNext();
	    			replaceWith(manager, new ArrayDecl(location, new Variable(type, name.content), sub));
	    			name.drop();
	    		} else {
	    			//FIXME either this should never happen in the user's sourcecode,
	    			//either it's a limitation that should be handled here
	    			manager.err("fixme:'Non-empty subscript after a TypeReference' not followed by a Name.. not supported yet", this);
	    		}
	    		
	    	} else if(next instanceof Name) {
	    		
	    		// Cool! A variable declaration =)
	    		next.drop();
	    		replaceWith(manager, new VariableDecl(location, new Variable(type, ((Name) next).content)));
	    		return;
	    		
	    	} else {
	    		
	    		break;
	    		
	    	}
    	}
		
		manager.queue(type, "Type assembling.");
		
	}

	/**
	 * @return the type referred to by this reference.
	 */
	public Type getType() {
		return type;
	}
	
}
