package org.ooc.nodes.numeric;

import java.io.IOException;

import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * An int literal, e.g. "64123" or "4_294_967_296"
 *
 * @author Amos Wenger
 */
public class IntLiteral extends NumberLiteral {
    
    private int value;
    
    /** The int type */
    public final static Type type = Type.baseType("Int");

    /**
     * Default constructor
     * @param location
     * @param value
     */
    public IntLiteral(FileLocation location, int value) {
        super(location);
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    @Override
	public void negate() {
        this.value = -value;
    }

	@Override
	public void writeToCSource(Appendable a) throws IOException {
		writeWhitespace(a);
		a.append(String.valueOf(value));
	}

}
