package org.ooc.nodes.types;

import java.io.IOException;

import org.ooc.nodes.others.VariableAccess;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A cast, ie.
 * <code>
 * NewType newTypedObject = (NewType) new OldType();
 * </code>
 *
 * @author Amos Wenger
 */
public class Cast extends VariableAccess {

    protected TypeReference ref;
    protected VariableAccess access;

    /**
     * Default constructor
     * @param location
     * @param ref
     * @param access
     */
    public Cast(FileLocation location, TypeReference ref, VariableAccess access) {
        super(location, new Variable(access.getType(), access.toString()));
        this.ref = ref;
        this.access = access;
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
        // Extra parenthesis for safety
        a.append("((");
        this.ref.writeToCSource(a);
        a.append(") ");
        access.writeToCSource(a);
        a.append(")");
    }
    
    
    @Override
	public Type getType() {
    	return getDestinationType();
    }
    
    /**
     * @return the destination type of the cast, ie. "what we're casting to"
     */
    public Type getDestinationType() {
    	return ref.getType();
	}
    
    /**
     * @return the source type of the cast, ie. "what we're casting from"
     */
    public Type getSourceType() {
    	return access.getType();
    }

}
