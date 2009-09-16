package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ArrayLiteral extends Literal implements MustBeUnwrapped, MustBeResolved {

	private static Type defaultType = NullLiteral.type;
	protected Type type = defaultType;
	protected NodeList<Expression> elements;
	
	public ArrayLiteral(Token startToken) {
		super(startToken);
		elements = new NodeList<Expression>(startToken);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	public NodeList<Expression> getElements() {
		return elements;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		type.accept(visitor);
		elements.accept(visitor);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public boolean isResolved() {
		return type != defaultType;
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(!elements.isEmpty()) {
			Iterator<Expression> iter = elements.iterator();
			Type innerType = iter.next().getType();
			
			while(iter.hasNext()) {
				Expression element = iter.next();
				if(!element.getType().fitsIn(innerType)) {
					throw new OocCompilationError(element, stack, "Encountered a "
							+element.getType()+" in a "+innerType+"* array literal.");
				}
			}
			
			this.type = new Type(innerType.name, innerType.pointerLevel + 1, startToken);
			type.setArray(true);
			type.resolve(stack, res, fatal);
		}
		
		return (type == defaultType) ? Response.LOOP : Response.OK; 
		
	}

	@Override
	public boolean unwrap(NodeList<Node> stack) throws IOException {
		
		int varDeclIndex = stack.find(VariableDecl.class);
		
		// if stack.size() - varDeclIndex > 3, we're nested in another varDecl
		//, thus we need to unwrap
		if(varDeclIndex == -1 || (stack.size() - varDeclIndex) > 3) {
			stack.peek().replace(this, new VariableDeclFromExpr(
					generateTempName("array"), this, startToken));
			return true;
		}
		
		return false;
		
	}

}
