package org.ooc.nodes.keywords;

import java.io.IOException;

import org.ooc.errors.CompilationFailedError;
import org.ubi.FileLocation;

/**
 * Allows reverse iteration on a range, e.g.
 * <code>
 * for(Int i: reverse 0..11) {
 *   printf("%d...", i);
 * }
 * printf("BAOOM!");
 * </code>
 * 
 * @author Amos Wenger
 */
public class ReverseKeyword extends Keyword {

	/**
	 * Default constructor
	 * @param location
	 */
	public ReverseKeyword(FileLocation location) {
		super(location);
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		throw new CompilationFailedError(location, "Misuse of the 'reverse' keyword, valid only before a range (e.g. reverse 0..100)");

	}

}
