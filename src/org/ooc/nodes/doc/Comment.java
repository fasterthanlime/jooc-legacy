package org.ooc.nodes.doc;

/**
 * Common interface for single, multi-line, and oocdoc comments
 * @author Amos Wenger
 */
public interface Comment {
	
	/**
	 * @return the content of this comment
	 */
	public String getContent();

}
