package org.ooc.nodes.numeric;

import java.io.IOException;

import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * A double literal, e.g. "3.14" or "69." or ".42"
 *
 * @author Amos Wenger
 */
public class DoubleLiteral extends NumberLiteral {

    private double value;
    
    private final static Type type = Type.baseType("Double");

    /**
     * Default constructor
     * @param location
     * @param value
     */
    public DoubleLiteral(FileLocation location, double value) {
        super(location);
        this.value = value;
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	writeWhitespace(a);
    	a.append(String.valueOf(value));
        // The l suffix means long double
        //a.append("l");
    }

    public Type getType() {
    	return type;
    }

    @Override
	public void negate() {
        value = -value;
    }

}
