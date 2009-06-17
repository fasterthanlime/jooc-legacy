package org.ooc.nodes.preprocessor;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * Base class for preprocessor directives, e.g. #ifdef, #define, #else, #pragma, etc.
 *
 * @author Amos Wenger
 */
public abstract class PreprocessorDirective extends SyntaxNode {

	/**
	 * Default constructor
	 * @param location
	 */
	public PreprocessorDirective(FileLocation location) {
		super(location);
	}

	@Override
	public String getDescription() {
    	return toString() + location;
    }
}
