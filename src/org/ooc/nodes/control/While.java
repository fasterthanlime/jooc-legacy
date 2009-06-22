package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.nodes.others.Parenthesis;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * A while loop
 * 
 * @author Amos Wenger
 */
public class While extends Parenthesis {
	
	/**
	 * Default constructor
	 * @param location
	 */
	public While(FileLocation location) {
		super(location);
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		writeWhitespace(a);
		a.append("while(");
        for(SyntaxNode node: nodes) {
            node.writeToCSource(a);
        }
        a.append(")");
		
	}

}
