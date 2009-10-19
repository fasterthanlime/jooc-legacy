package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Match extends Expression implements MustBeResolved {

	private Type type;
	protected NodeList<Case> cases;
	protected Expression expr;

	public Match(Expression expr, Token startToken) {
		super(startToken);
		if(expr == null) {
			this.expr = new BoolLiteral(true, this.startToken);
		} else {
			this.expr = expr;
		}
		this.cases = new NodeList<Case>();
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
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
		expr.accept(visitor);
		cases.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}
	
	public NodeList<Case> getCases() {
		return cases;
	}
	
	public Expression getExpr() {
		return expr;
	}

	public Type getType() {
		return type;
	}

	public boolean isResolved() {
		return false;
	}
 
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Node parent = stack.peek();
		
		if(type == null) resolveType(stack, res, fatal);
		
		if(type == null) {
			if(fatal) {
				throw new OocCompilationError(this, stack, "Couldn't figure out ");
			}
			return Response.LOOP;
		}
		
		if(parent instanceof Line) {
			// alright =)
		} else if(parent instanceof VariableDeclAtom) {
			VariableDecl vDecl = (VariableDecl) stack.get(stack.find(VariableDecl.class));
			vDecl.setType(vDecl.getType()); // fixate the type
			addAfterLine(stack, this);
			VariableAccess varAcc = new VariableAccess(vDecl, parent.startToken);
			toAssign(stack, varAcc);
			stack.peek().replace(this, null);
		} else if(parent instanceof Assignment) {
			// it's alright =)
		} else {
			// we're being USED! as an expression somewhere, let's unwrap to a varDecl.
			VariableDecl varDecl = new VariableDecl(getType(), false, startToken);
			varDecl.getAtoms().add(new VariableDeclAtom(generateTempName("match", stack), null, startToken));
			addBeforeLine(stack, varDecl);
			addBeforeLine(stack, this);
			VariableAccess varAcc = new VariableAccess(varDecl, startToken);
			toAssign(stack, varAcc);
			stack.peek().replace(this, varAcc);
		}
		
		return Response.OK;
		
	}

	private void resolveType(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		int funcIndex = stack.find(FunctionDecl.class);
		int returnIndex = stack.find(ValuedReturn.class);
		
		if(funcIndex != -1 && returnIndex != -1) {
			FunctionDecl funcDecl = (FunctionDecl) stack.get(funcIndex);
			if(funcDecl.getReturnType().isGeneric()) {
				type = funcDecl.getReturnType();
			}
		}
		
		if(type == null) {
		
			// TODO make it more intelligent e.g. cycle through all cases and
			// check that all types are compatible and find a common denominator
			if(cases.isEmpty()) return;
			Case first = cases.getFirst();
			if(first.getBody().isEmpty()) return;
			Statement statement = first.getBody().getFirst().getStatement();
			if(!(statement instanceof Expression)) return;
			type = ((Expression) statement).getType();
			
		}
		
	}

	private void toAssign(NodeList<Node> stack, VariableAccess varAcc)
			throws OocCompilationError {
		for(Case case1: cases) {
			if(case1.getBody().isEmpty()) {
				throw new OocCompilationError(case1, stack, "Empty case in a match used as an expression!");
			}
			Line last = case1.getBody().getLast();
			if(!(last.getStatement() instanceof Expression)) {
				throw new OocCompilationError(case1, stack, "In a match used as an expression, last statement of a case isn't an expression!");	
			}
			case1.getBody().replace(last, new Line(new Assignment(varAcc, (Expression) last.getStatement(), last.startToken)));
		}
	}
	
	@Override
	public String toString() {
		return "match "+expr+", with "+cases.size()+" cases";
	}

}
