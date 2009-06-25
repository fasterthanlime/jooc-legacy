package org.ooc.nodes.types;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.parsers.TypeParser;
import org.ubi.FileLocation;

/**
 * Type definition, "the C way". Effectively represents a "typedef" as
 * known in the C language.
 * 
 * @author Amos Wenger
 */
public class TypeDef extends SyntaxNode {

    protected final Type type;
    protected final String name;

    /**
     * Default constructor
     * @param location
     * @param type
     * @param name
     */
    public TypeDef(FileLocation location, Type type, String name) {
    	
        super(location);
        this.type = type;
        this.name = name;
        
    }

    
    public void writeToCSource(Appendable a) throws IOException {
    	
    	/** @see writeToCHeader */
    	
    }

    
    @Override
	public void writeToCHeader(Appendable a) throws IOException {
    	
    	writeWhitespace(a);
        a.append("typedef ");
        type.writeToCSource(a);
        if(type.getPointerLevel() == 0) {
            a.append(' ');
        }
        a.append(name);
        a.append(";\n");
        
    }
    
    
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    
    	if(!type.isResolved) {
    		manager.queue(type, "Typedef's type resolving");
    		manager.queue(this, "Typedef '"+name+"' of type '"+type.name+"', can't be resolved!");
    	} else {
    		TypeParser.addType(name);
    	}
    	
    }
    
    
    @Override
	protected boolean isSpaced() {
    	return false;
    }

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
