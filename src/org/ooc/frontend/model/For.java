package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

/**
 * A regular for, e.g.
 * <code>
 * for(init; test; iter) {
 *     body
 * }
 * </code>
 * 
 * @author Amos Wenger
 */
public class For extends ControlStatement {
	
	protected Statement init;
	protected Expression test;
	protected Statement iter;
	
	public For(Statement init, Expression test, Statement iter, Token startToken) {
		super(startToken);
		this.init = init;
		this.test = test;
		this.iter = iter;
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == init) {
			init = (Statement) kiddo;
			return true;
		}
		if(oldie == test) {
			test = (Expression) kiddo;
			return true;
		}
		if(oldie == iter) {
			iter = (Statement) kiddo;
			return true;
		}
		return false;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		init.accept(visitor);
		test.accept(visitor);
		iter.accept(visitor);
		body.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}

	public Statement getInit() {
		return init;
	}

	public Expression getTest() {
		return test;
	}

	public Statement getIter() {
		return iter;
	}
	
	

}
