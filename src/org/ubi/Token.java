package org.ubi;

/**
 * A token is an interesting part of a text, e.g. a keyword
 * 
 * @author Amos Wenger
 */
public class Token {

	/** the start index of a keyword */
	public int start;
	
	/** the length of a keyword */
	public int length;
	
	/**
	 * Null constructor, sets start and end to -1
	 */
	public Token() {
		this.start = -1;
		this.length = -1;
	}
	
	/**
	 * Default constructor
	 * @param start
	 * @param end
	 */
	public Token(int start, int end) {
		this.start = start;
		this.length = end - start;
	}
	
}
