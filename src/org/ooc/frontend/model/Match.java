package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.hobgoblins.Resolver;

public class Match extends Expression implements MustBeResolved {

	protected NodeList<Case> cases;
	protected Expression expr;
	protected VariableAccess varAcc;

	public Match(Expression expr, Token startToken) {
		super(startToken);
		if(expr == null) {
			this.expr = new BoolLiteral(true, this.startToken);
		} else {
			this.expr = expr;
		}
		this.cases = new NodeList<Case>();
		this.varAcc = null;
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == varAcc) {
			varAcc = (VariableAccess) kiddo;
			return true;
		}
		
		if(oldie == expr) {
			expr = (Expression) kiddo;
			return true;
		}
		return false;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		if(varAcc != null) {
			
		}
		expr.accept(visitor);
		cases.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}
	
	public NodeList<Case> getCases() {
		return cases;
	}
	
	public VariableAccess getVarAcc() {
		return varAcc;
	}
	
	public Expression getExpr() {
		return expr;
	}

	public Type getType() {
		// TODO make it more intelligent e.g. cycle through all cases and
		// check that all types are compatible and find a common denominator
		if(cases.isEmpty()) return null;
		Case first = cases.getFirst();
		if(first.getBody().isEmpty()) return null;
		Statement statement = first.getBody().getFirst().getStatement();
		if(!(statement instanceof Expression)) return null;
		return ((Expression) statement).getType();
	}

	public boolean isResolved() {
		return false;
	}
 
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Node parent = stack.peek();
		
		if(parent instanceof Line) {
			// alright =)
		} else if(parent instanceof VariableDeclAtom) {
			VariableDecl vDecl = (VariableDecl) stack.get(stack.find(VariableDecl.class));
			vDecl.setType(vDecl.getType()); // fixate the type
			stack.peek().replace(this, null);
			addAfterLine(stack, this);
			this.varAcc = new VariableAccess(vDecl, parent.startToken);
		} else {
			// we're being USED! as an expression somewhere, let's unwrap to a varDecl.
			VariableDecl varDecl = new VariableDecl(getType(), false, startToken);
			varDecl.getAtoms().add(new VariableDeclAtom(generateTempName("match"), null, startToken));
			addBeforeLine(stack, varDecl);
			addBeforeLine(stack, this);
			this.varAcc = new VariableAccess(varDecl, startToken);
			stack.peek().replace(this, varAcc);
		}
		
		return Response.OK;
		
	}

}
