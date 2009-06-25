package org.ooc.nodes.text;

import java.io.IOException;

import org.ooc.nodes.others.Literal;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

/**
 * A string literal, e.g. "the quick red firefox", "'jumps' over" or "the lazy IE\n"
 *
 * @author Amos Wenger
 */
public class StringLiteral extends Literal {

	private String content;
	/** the String type */
	public final static Type type = Type.baseType("String");
	
	/**
	 * Default constructor
	 * @param location
	 * @param content
	 */
    public StringLiteral(FileLocation location, String content) {
    	
        super(location);
        this.content = content;
        
    }

    
    public void writeToCSource(Appendable a) throws IOException {
        writeWhitespace(a);
        a.append('"');
        a.append(SourceReader.spelled(content));
        a.append('"');
    }

    public Type getType() {
        return type; // The users won't necessarily make a typedef.. be warned my compiler friend.
    }

}
