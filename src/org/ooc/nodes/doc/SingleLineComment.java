package org.ooc.nodes.doc;

import java.io.IOException;

import org.ooc.nodes.others.LineSeparator;
import org.ubi.FileLocation;

/**
 * A single line comment, e.g.
 * <code>
 * // Man, that's some sweet comment. Isn't life beautiful as a comment? 
 * </code>
 *
 * @author Amos Wenger
 */
public class SingleLineComment extends LineSeparator implements Comment {

	private final String content;
	
	/**
	 * Default constructor
	 * @param location
	 * @param content
	 */
    public SingleLineComment(FileLocation location, String content) {
        super(location);
        this.content = content.replace('\n', ' ');
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	writeWhitespace(a);
        a.append("// ");
        a.append(content);
        a.append("\n");
    }
    
    
	@Override
	public String getDescription() {
    	return toString()+location;
    }

	
	public String getContent() {
		return content;
	}
	
	
	@Override
	protected boolean isSpaced() {
		return false;
	}

}
