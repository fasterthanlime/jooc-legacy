package org.ooc.nodes.control;

import java.io.IOException;
import java.util.ArrayList;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.functions.MemberFunctionCall;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.others.Block;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A foreach in a collection
 *
 * @author Amos Wenger
 */
public class ForEach extends SyntaxNodeList {

    protected VariableDecl decl;
    protected VariableAccess collection;
    protected Variable iterator;
    
    protected Block init;

    /**
     * Default constructor
     * @param location
     * @param decl
     * @param collection
     */
    public ForEach(FileLocation location, VariableDecl decl, VariableAccess collection) {
    	
        super(location);
        this.decl = decl;
        this.collection = collection;
        
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	
    	writeWhitespace(a);
    	
    	init.writeToCSourceAsChild(this, a);
        
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {

        Scope parentScope = getParent().getNearest(Scope.class);

        iterator = parentScope.generateTempVariable(new Type(location, null, "Iterator"), "iter");
        init = new Block(location);
        init.add(new VariableDecl(location, iterator));
        init.add(new Assignment(location));
        init.add(new MemberFunctionCall(location, "iterator", new ArrayList<SyntaxNode>(), collection));
        init.add(new LineSeparator(location));
        
        While wh = new While(location);
        wh.add(new MemberFunctionCall(location, "hasNext", new ArrayList<SyntaxNode>(),
        		new VariableAccess(location, iterator)));
        init.add(wh);
        
        init.setContext(this);
        manager.queueRecursive(init, "Assembling foreach initializers..");

        if(!(getParent().getNext(this) instanceof Scope)) {
            manager.err("Expected a scope {} after a foreach", this);
            return;
        }
        Scope forBody = (Scope) getParent().getNext(this);
        
        forBody.addToHead(new LineSeparator(location));
        forBody.addToHead(new MemberFunctionCall(location, "next", new ArrayList<SyntaxNode>(),
        		new VariableAccess(location, iterator)));
        forBody.addToHead(new Assignment(location));
        forBody.addToHead(decl);

        decl.variable.type.setContext(getParent());
        manager.queue(decl, "Assembling the iterating variable declaration in a ForEach");
        
    }

}
