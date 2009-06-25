package org.ooc.nodes.preprocessor;

import java.io.IOException;

import org.ubi.FileLocation;

/**
 * A preprocessor directive which is currently not processed by the ooc
 * compiler, e.g. #ifdef, #else, #if, #endif, #pragma, #warn, #error, etc. 
 * 
 * @author Amos Wenger
 */
public class RawPreprocessorDirective extends PreprocessorDirective {

	String content;
	
	/**
	 * Default constructor
	 * @param location
	 * @param content
	 */
    public RawPreprocessorDirective(FileLocation location, String content) {
        super(location);
        this.content = content;
    }

    
    public void writeToCSource(Appendable a) throws IOException {
        // Well, nothing sir.
    }
    
    
    @Override
	public void writeToCHeader(Appendable a) throws IOException {
    	a.append('#');
        a.append(content);
    }
	
}
