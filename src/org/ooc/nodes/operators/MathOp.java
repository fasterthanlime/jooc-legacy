package org.ooc.nodes.operators;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.interfaces.LinearNode;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.others.RawCode;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * A binary math operation
 * 
 * @author Amos Wenger
 */
public class MathOp extends SyntaxNodeList implements Typed, LinearNode {

	/**
	 * Default constructor
	 * @param location
	 * @param prev
	 * @param next
	 */
	public MathOp(FileLocation location, SyntaxNode prev, SyntaxNode next, String symbol) {
		super(location);
		prev.moveTo(this);
		add(new RawCode(location, symbol));
		next.moveTo(this);
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
		writeWhitespace(a);
		for(SyntaxNode node: nodes) {
            node.writeToCSource(a);
        }
	}
	
	@Override
	protected boolean isSpaced() {
		return true;
	}
	
	@Override
	public Type getType() {

		SyntaxNode prev = nodes.get(0);
		SyntaxNode next = nodes.get(2);
		
		if(prev instanceof Typed && next instanceof Typed) {
			return ((Typed) prev).getType();
		}
		
		return Type.UNKNOWN;
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {

		assembleAll(manager);
		
	}

}
