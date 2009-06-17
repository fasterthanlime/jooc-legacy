package org.ooc.nodes.control;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.array.Subscript;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.numeric.IntLiteral;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.others.VariableDecl;
import org.ubi.FileLocation;

/**
 * A foreach in a collection
 *
 * @author Amos Wenger
 */
public class ForEach extends SyntaxNodeList {

    private VariableDecl decl;
    private Typed collection;
    private String index;

    /**
     * Default constructor
     * @param location
     * @param decl
     * @param collection
     */
    public ForEach(FileLocation location, VariableDecl decl, Typed collection) {
    	
        super(location);
        this.decl = decl;
        this.collection = collection;
        
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	
    	writeWhitespace(a);
        a.append("for(int ");
        a.append(index);
        a.append(" = 0; ");
        a.append(index);
        a.append(" < (sizeof(");
        a.append(collection.toString().trim());
        a.append(") / sizeof(");
        a.append(collection.getType().toString());
        a.append(")); ");
        a.append(index);
        a.append("++) ");
        
    }

    @Override
    protected void assembleImpl(AssemblyManager manager) {

        Scope parentScope = getParent().getNearest(Scope.class);

        index = parentScope.generateTempVariable(IntLiteral.type, "i").getName();

        if(!(getParent().getNext(this) instanceof Scope)) {
            manager.err("Expected a scope {} after a foreach", this);
            return;
        }
        Scope forBody = (Scope) getParent().getNext(this);
        forBody.addToHead(new LineSeparator(location));
        Subscript subscript = new Subscript(location);
        subscript.add(new Name(location, index));
        forBody.addToHead(subscript);
        forBody.addToHead(new Name(location, collection.toString()));
        forBody.addToHead(new Assignment(location));
        forBody.addToHead(decl);

        decl.variable.type.setContext(getParent());
        manager.queue(decl, "Assembling the iterating variable declaration in a ForEach");
        
    }

}
