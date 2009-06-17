package org.ooc.nodes.preprocessor;

import java.io.IOException;

import org.ubi.FileLocation;

/**
 * A macro is a define with arguments in C preprocessing language
 * 
 * @author Amos Wenger
 */
public class Macro extends Define {

	private String args;

	/**
	 * Default constructor
	 * @param location
	 * @param name
	 * @param args
	 * @param content
	 */
	public Macro(FileLocation location, String name, String args, String content) {
		super(location, name, content);
		this.args = args;
	}

	@Override
	public void writeToCHeader(Appendable a) throws IOException {
		
		a.append("\n#define ");
		a.append(name);
		a.append('(');
		a.append(args);
		a.append(") ");
		a.append(content);
		a.append('\n');

	}
	
}
