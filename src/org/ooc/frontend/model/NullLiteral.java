package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;


public class NullLiteral extends Literal {

	public static Type type = new Type("Pointer", Token.defaultToken);
	
	public NullLiteral(Token startToken) {
		super(startToken);
		// blahbedi blah, blahbidi blah, eeky eeky, ooogoozooooooooo :(
	}
	
	public Type getType() {
		return type;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return false;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

}
