package org.ooc.nodes.numeric;

import java.io.IOException;

import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * A float literal, e.g. "0f" or ".0988f" or "3.14159f"
 *
 * @author Amos Wenger
 */
public class FloatLiteral extends NumberLiteral {

	private float value;
	
	private final static Type type = Type.baseType("Float");

	/**
	 * Default constructor
	 * @param location
	 * @param value
	 */
    public FloatLiteral(FileLocation location, float value) {
        super(location);
        this.value = value;
    }

    public Type getType() {
    	return type;
    }

    @Override
	public void negate() {
        value = -value;
    }
    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
		writeWhitespace(a);
		a.append(String.valueOf(value));
		a.append("f");
	}

}
