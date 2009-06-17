package org.ooc.nodes.keywords;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.CompilationFailedError;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Keyword for making a class inherit from a base class, be it abstract
 * or not.
 *
 * @author Amos Wenger
 */
public class FromKeyword extends Keyword {
	
	private final String superClass;
	
	/**
	 * Default constructor
	 * @param location
	 * @param superClass
	 */
    public FromKeyword(FileLocation location, String superClass) {
        super(location);
        this.superClass = superClass;
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	// Lalalaaalaaaaaa..
    }
    
    /**
     * 
     * @param reader
     * @return
     * @throws EOFException
     */
    public static FromKeyword tryToRead(SourceReader reader) throws EOFException {
        int mark = reader.mark();
        try {
			return FromKeyword.read(reader);
        } catch(SyntaxError e) {
            reader.reset(mark);
            return null;
        }
    }

    private static FromKeyword read(SourceReader reader) throws SyntaxError, EOFException {

        reader.skipWhitespace();
        FileLocation location = reader.getLocation();
        if(reader.matches("extends", true) && reader.hasWhitespace(true)) {
            throw new CompilationFailedError(location, "The 'extends' keyword is deprecated, use the 'from' keyword instead.");
        }
        
        if(!(reader.matches("from", true) && reader.hasWhitespace(true))) {
        	return null;
        }

        reader.skipWhitespace();
        String name = reader.readName();
        if(name.isEmpty()) {
            reader.err("Expected class name after 'from'. What class do you want to derive from?");
        }
        
        return new FromKeyword(reader.getLocation(), name);

    }

	/**
	 * @return the super class
	 */
	public String getSuperClass() {
		return superClass;
	}

}
