package org.ooc.nodes.interfaces;

import org.ooc.nodes.doc.OocDocComment;

/**
 * Implemented by anything commentable, e.g. a function definition 
 * 
 * @author Amos Wenger
 */
public interface Commentable {

	/**
	 * Assign a comment to something commentable
	 * @param oocDocComment
	 */
	public void setComment(OocDocComment oocDocComment);
	
}
