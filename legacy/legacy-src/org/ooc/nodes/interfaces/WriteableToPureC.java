package org.ooc.nodes.interfaces;

import java.io.IOException;

/**
 * Implemented by any class (often SyntaxNode) which is willing to be "serializable" to pure C.
 * 
 * @author Amos Wenger
 */
public interface WriteableToPureC {

	/**
	 * Appends C source code for this node to an appendable (e.g. FileWriter,
	 * or StringBuilder)
	 * @param a
	 * @throws IOException
	 */
    public void writeToCSource(Appendable a) throws IOException;
    
    
    /**
	 * Appends C header code for this node to an appendable (e.g. FileWriter,
	 * or StringBuilder)
	 * @param a
	 * @throws IOException
	 */
    public void writeToCHeader(Appendable a) throws IOException;

}
