package org.ooc.nodes.text;

import java.io.IOException;

import org.ooc.nodes.others.Literal;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

/**
 * A character literal, e.g. 'c' or '\n'
 *
 * @author Amos Wenger
 */
public class CharLiteral extends Literal {

	private String content;
	
	private final static Type type = Type.baseType("Char");
	
	/**
	 * Default constructor
	 * @param location
	 * @param content
	 */
    public CharLiteral(FileLocation location, String content) {
        super(location);
        this.content = content;
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
        writeWhitespace(a);
        a.append("'");
        a.append(SourceReader.spelled(this.content));
        a.append("'");
    }

    public Type getType() {
        return type;
    }

}
