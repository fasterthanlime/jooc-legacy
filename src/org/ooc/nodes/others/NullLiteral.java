package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * A null literal, e.g. can only be 'null'
 * Corresponds to C's "NULL"
 * 
 * @author Amos Wenger
 */
public class NullLiteral extends Literal {

	private final static Type type = Type.baseType("null");
	
	/**
	 * Default constructor
	 * 
	 * @param location
	 */
	public NullLiteral(FileLocation location) {
		super(location);
	}
	
	@Override
	protected boolean isSpaced() {
		return true;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
		writeWhitespace(a);
		a.append("NULL");
	}

}
