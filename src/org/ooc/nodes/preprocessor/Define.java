package org.ooc.nodes.preprocessor;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ubi.FileLocation;

/**
 * The #define preprocessor directive 
 * 
 * @author Amos Wenger
 */
public class Define extends PreprocessorDirective {

	/** The name of the define */
	public String name;
	
	/** The content of the define (e.g. replacement text) */
	public String content;
	
	/**
	 * Default constructor
	 * @param location
	 * @param name
	 * @param content
	 */
	public Define(FileLocation location, String name, String content) {
		super(location);
		this.name = name;
		this.content = content.trim();
	}
	
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {

		manager.getContext().addDefineSymbol(this);
		lock();
		
	}

	
	public void writeToCSource(Appendable a) throws IOException {
		// Tralala.
	}
	
	
	@Override
	public void writeToCHeader(Appendable a) throws IOException {
		
		a.append("\n#define ");
		a.append(name);
		a.append(' ');
		a.append(content);
		a.append('\n');

	}
}
