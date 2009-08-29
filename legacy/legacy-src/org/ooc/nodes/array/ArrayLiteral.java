package org.ooc.nodes.array;

import java.io.IOException;

import org.ooc.nodes.control.Scope;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.others.Literal;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * An array literal, e.g. {1, 2, 3, 4}
 *
 * @author Amos Wenger
 */
public class ArrayLiteral extends SyntaxNodeList {

    protected Type type;

    /**
     * Default constructor
     * @param location
     * @param typed
     * @param scope
     */
    public ArrayLiteral(FileLocation location, Typed typed, Scope scope) {
    	
        super(location);
        this.addAll(scope);
        this.type = typed.getType();
        
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	
        a.append("{");
        for(SyntaxNode node: this.nodes) {
            if(node instanceof FunctionCall || node instanceof Name || node instanceof Literal) {
                a.append("(");
                a.append(this.type.name);
                a.append(") ");
            }
            node.writeToCSource(a);
        }
        a.append("}");
        
    }

}
