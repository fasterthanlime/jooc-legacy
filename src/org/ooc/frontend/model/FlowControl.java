package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class FlowControl extends ControlStatement {

	public static enum Mode {
		BREAK,
		CONTINUE,
	}

	private Mode mode;
	
	public FlowControl(Mode mode, Token startToken) {
		super(startToken);
		this.mode = mode;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {}

	public boolean hasChildren() {
		return false;
	}
	
	public Mode getMode() {
		return mode;
	}

	public String getKeyword() {
		switch(mode) {
		case CONTINUE:
			return "continue";
		case BREAK: default:
			return "break";
		}
	}

}
