package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * The 'return' keyword, to pass values out a function.
 *
 * @author Amos Wenger
 */
public class Return extends SyntaxNode {

	/**
	 * Default constructor
	 * @param location
	 */
    public Return(FileLocation location) {
        super(location);
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
        writeWhitespace(a);
        a.append("return");
    }

}
