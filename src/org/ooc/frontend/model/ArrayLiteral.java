package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class ArrayLiteral extends Literal implements MustBeUnwrapped {

	private FunctionCall outerCall = null;
	private int outerArgIndex = -1;
	
	//private static Type defaultType = NullLiteral.type;
	private static Type defaultType = null;
	private Type innerType = null;
	private Type type = defaultType;
	
	protected NodeList<Expression> elements;
	
	public ArrayLiteral(Token startToken) {
		super(startToken);
		elements = new NodeList<Expression>(startToken);
	}
	
	@Override
	public Expression getGenericOperand() {
		System.out.println("Should get genericOperand of arrlit "+this);
		return super.getGenericOperand();
	}
	
	public int getDepth() {
		if(elements.isEmpty() || !(elements.getFirst() instanceof ArrayLiteral)) return 1;
		return 1 + ((ArrayLiteral) elements.getFirst()).getDepth();
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		} else if(oldie == innerType) {
			innerType = (Type) kiddo;
			return true;
		}
		
		return false;
	}

	public Type getType() {
		return type;
	}
	
	public Type getInnerType() {
		return innerType;
	}
	
	public NodeList<Expression> getElements() {
		return elements;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		if(type != null) type.accept(visitor);
		elements.accept(visitor);
	}

	public boolean hasChildren() {
		return true;
	}

	@Override
	public boolean isResolved() {
		return type != defaultType && super.isResolved();
	}
	
	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(type != defaultType) return Response.OK;
		
		if(!elements.isEmpty()) {
			
			Iterator<Expression> iter = elements.iterator();
			
			// try to determine the innerType from an outer call
			if(outerCall != null && outerArgIndex != -1) {
				FunctionDecl impl = outerCall.getImpl();
				if(impl == null) {
					if(fatal) {
						throw new OocCompilationError(this, stack, "Couldn't resolve type of an array literal because" +
								" the function call it's in hasn't been resolved.");
					}
					return Response.LOOP;
				}
				Argument arg = impl.getArguments().get(outerArgIndex);
				
				ArrayLiteral lit = this;
				int pointerLevel = 0;
				while(lit != null) {
					Node first = lit.getElements().getFirst();
					if(first instanceof ArrayLiteral) {
						lit = (ArrayLiteral) first;
						pointerLevel++;
					} else {
						lit = null;
					}
				}
				innerType = new Type(arg.getType().getName(), arg.getType().startToken);
				innerType.setPointerLevel(pointerLevel);
			}
			
			// try to determine the innerType from the first element
			if(innerType == null) {
				Expression first = iter.next();
				innerType = first.getType();
			}
			
			// if we didn't resolve it && fatal, we're screwed :/ 
			if(innerType == null) {
				if(fatal) {
					throw new OocCompilationError(this, stack, "Couldn't resolve type of an array literal");
				}
				return Response.LOOP;
			}
			
			while(iter.hasNext()) {
				Expression element = iter.next();
				if(element.getType() == null) {
					if(fatal) {
						throw new OocCompilationError(element, stack, "Couldn't resolve type of "
								+element+" in an "+innerType+" array literal");
					}
					return Response.LOOP;
				}
				if(!element.getType().fitsIn(innerType)) {
					throw new OocCompilationError(element, stack, "Encountered a "
							+element.getType()+" in a "+innerType+"[] array literal.");
				}
			}

			this.type = new Type(innerType.name, innerType.pointerLevel + 1, startToken);
			type.getTypeParams().addAll(innerType.getTypeParams());
			type.setArray(true);
			stack.push(this);
			type.resolve(stack, res, fatal);
			innerType.resolve(stack, res, fatal);
			stack.pop(this);
		}
		
		if(type == defaultType && fatal) {
			throw new OocCompilationError(this, stack, "Couldn't figure out type of ArrayLiteral with elements "+elements);
		}
		return (type == defaultType) ? Response.LOOP : super.resolve(stack, res, fatal);
		
	}

	@SuppressWarnings("unchecked")
	public boolean unwrap(NodeList<Node> stack) throws IOException {

		// try to determine the innerType from the outer requirements
		if(outerCall == null || outerArgIndex == -1) {
			ArrayLiteral lit = this;
			int litIndex = stack.find(ArrayLiteral.class);
			int callIndex = stack.find(FunctionCall.class);
			if(callIndex != -1) {
				if(litIndex != -1 && litIndex > callIndex) {
					lit = (ArrayLiteral) stack.get(litIndex);
				}
				outerCall = (FunctionCall) stack.get(callIndex);
				outerArgIndex = ((NodeList<Node>) stack.get(callIndex + 1)).indexOf(lit);
			}
		}
		
		if(stack.peek() instanceof Cast || stack.peek() instanceof Foreach) {
			return false;
		}
		
		if(stack.peek(2) instanceof ArrayLiteral) {
			return false;
		}
		
		int varDeclIndex = stack.find(VariableDecl.class);
		
		// if stack.size() - varDeclIndex > 3, we're nested in another varDecl
		//, thus we need to unwrap
		if(varDeclIndex == -1 || (stack.size() - varDeclIndex) > 3) {
			stack.peek().replace(this, new VariableDecl(
					null, generateTempName("array", stack), this, startToken, stack.getModule()));
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		boolean isFirst = true;
		for(Statement element : elements) {
			if(isFirst) isFirst = false;
			else        sb.append(", ");
			sb.append(element.toString());
		}
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public boolean isConstant() {
		return false;
	}

}
