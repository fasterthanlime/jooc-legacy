package org.ooc.nodes.numeric;

import java.io.IOException;

import org.ooc.nodes.others.Literal;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * A boolean literal can be 'true' or 'false'
 * 
 * @author Amos Wenger
 */
public class BooleanLiteral extends Literal {

	private boolean value;
	
	private final static Type type = Type.baseType("boolean");

	/**
	 * Default constructor
	 * @param location
	 * @param value
	 */
	public BooleanLiteral(FileLocation location, boolean value) {
		super(location);
		this.value = value;
	}

	
	public Type getType() {
		return type;
	}

	
	public void writeToCSource(Appendable a) throws IOException {
		writeWhitespace(a);
		a.append(Boolean.toString(value));
	}

}
