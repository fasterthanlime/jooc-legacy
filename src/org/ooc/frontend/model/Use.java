package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.parser.UseDefParser;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.UseDef;
import org.ooc.middle.hobgoblins.Resolver;
import org.ubi.CompilationFailedError;

public class Use extends Node implements MustBeResolved {

	protected String identifier;
	protected UseDef useDef;
	
	public Use(String name, Token startToken) {
		super(startToken);
		this.identifier = name;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String name) {
		this.identifier = name;
	}
	
	public UseDef getUseDef() {
		return useDef;
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

	public boolean isResolved() {
		return useDef != null;
	}

	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		try {
			useDef = UseDefParser.parse(identifier, res.params);
		} catch (Exception e) {
			throw new CompilationFailedError(e);
		}
		if(useDef == null && fatal) {
			throw new OocCompilationError(this, stack, "Couldn't find use "+identifier+" in sourcepath.");
		}
		return (useDef == null) ? Response.LOOP : Response.OK;
	}

}
