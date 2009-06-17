package org.ooc.nodes.doc;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.Commentable;
import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * ooc's equivalent for javadoc/doxygen. The syntax is largely javadoc-ish
 * 
 * @author Amos Wenger
 */
public class OocDocComment extends MultiLineComment {

	/**
	 * Constructor
	 * @param location
	 * @param before
	 * @param content
	 * @param after
	 */
	public OocDocComment(FileLocation location, String before, String content, String after) {
		
		super(location, before, content, "\n");
		
	}
	
	@Override
	protected String getPrelude() {
	
		return "/**";
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		SyntaxNode next = getNext();
		if(next instanceof Commentable) {
			((Commentable) next).setComment(this);
			drop();
		}
		
	}
	
}
