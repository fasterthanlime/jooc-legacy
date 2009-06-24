package org.ooc.nodes.clazz;

import java.io.IOException;

import org.ooc.features.core.StaticClassAccessFeature;
import org.ooc.nodes.types.TypeReference;
import org.ubi.FileLocation;

/**
 * A reference to a class, e.g. "MyClass". A good usage of this node can
 * be seen in @link {@link StaticClassAccessFeature} where the trio
 * (ClassReference, Dot, Name) is tested for.
 *
 * @author Amos Wenger
 */
public class ClassReference extends TypeReference {

	/** The class we're refering to */
    public ClassDef classDef;

    /**
     * Default constructor
     * @param location
     * @param clazz
     */
    public ClassReference(FileLocation location, ClassDef classDef) {
        super(location, classDef.clazz.getType());
        this.classDef = classDef;
    }
    
    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	writeWhitespace(a);
    	if(classDef.clazz.isCover) {
    		a.append(classDef.clazz.simpleName);
    	} else {
    		a.append(classDef.clazz.underName);
    	}
    }
    
    @Override
    protected boolean isSpaced() {

    	return true;
    	
    }
    
    @Override
    public String getDescription() {
    
    	return "Class reference to " + classDef.clazz.fullName;
    	
    }
    
    /*
    @Override
    protected void assembleImpl(AssemblyManager manager) {
    	
    	// TODO add ability to deal with fully-qualified class names. In other words, the length
    	// of the added token should be the length of what is in the source, not always
    	// the length of the simpleName.
    	TypedToken token = new TypedToken(TokenType.CLASS_REFERENCE);
    	token.node = this;
    	token.start = location.getIndex();
    	token.length = clazz.simpleName.length();
		manager.context.addTokenCopy(token);
		
    }
    */

}
