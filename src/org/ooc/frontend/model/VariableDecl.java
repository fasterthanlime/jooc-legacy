package org.ooc.frontend.model;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeUnwrapped;
import org.ooc.frontend.model.interfaces.Versioned;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class VariableDecl extends Declaration implements MustBeUnwrapped, PotentiallyStatic, Versioned {

	private VersionBlock version = null;

	protected boolean isConst;
	protected boolean isStatic;
	protected boolean isProto;
	protected boolean isGlobal;
	
	protected Type type;
	protected TypeDecl typeDecl;
	
	protected Expression expression;
	protected Assignment assign;

	public VariableDecl(Type type, String name, Token startToken, Module module) {
		this(type, name, null, startToken, module);
	}
	
	public VariableDecl(Type type, String name, Expression expression, Token startToken, Module module) {
		super(name, startToken, module);
		this.type = type;
		this.expression = expression;
	}
	
	public String getFullName() {
		StringBuilder sB = new StringBuilder();
		try {
			writeFullName(sB);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sB.toString();
	}
	
	public boolean isArg() {
		return false;
	}

	public void writeFullName(Appendable dst) throws IOException {
		
		/*
		if(externName != null && externName.length() > 0) {
			dst.append(externName);
		} else {
		*/
		if(isUnmangled()) {
			dst.append(getUnmangledName());
		} else if(isExtern()) {
			if(isExternWithName()) {
				dst.append(getExternName());
			} else {
				dst.append(getName());
			}
		} else if(isGlobal()) {
			if(module != null) {
				dst.append(module.getMemberPrefix());
			}
			dst.append(getName());
		} else {
			dst.append(getName());
		}
		//}
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		return typeDecl;
	}
	
	public void setTypeDecl(TypeDecl typeDecl) {
		this.typeDecl = typeDecl;
		if(type instanceof FuncType) {
			FuncType funcType = (FuncType) type;
			funcType.getDecl().setTypeDecl(typeDecl);
		}
	}
	
	public boolean isMember() {
		return typeDecl != null;
	}
	
	public boolean isConst() {
		return isConst;
	}
	
	public void setConst(boolean isConst) {
		this.isConst = isConst;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isProto() {
		return isProto;
	}
	
	public void setProto(boolean isProto) {
		this.isProto = isProto;
	}

	public boolean isGlobal() {
		return isGlobal;
	}
	
	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}
	
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	public void acceptChildren(Visitor visitor) throws IOException {
		if(getType() != null) getType().accept(visitor);
		if(expression != null) expression.accept(visitor);
	}

	public boolean unwrap(NodeList<Node> stack) {
		return unwrapToVarAcc(stack);
	}

	@SuppressWarnings("unchecked")
	public boolean unwrapToVarAcc(NodeList<Node> stack) {

		Node parent = stack.peek();
		Node grandpa = stack.get(stack.size() - 2);
		
		if(parent instanceof Line
		|| grandpa instanceof Module
		|| grandpa instanceof FunctionDecl
		|| grandpa instanceof TypeDecl
		) {
			return false;
		}
		
		VariableAccess varAcc = new VariableAccess(name, startToken);
		varAcc.setRef(this);
		if(!parent.replace(this, varAcc)) {
			Thread.dumpStack();
			throw new OocCompilationError(this, stack, "Couldn't replace \n"+this+" with \n"+varAcc
					+"in \n"+parent);
		}
		
		if(parent instanceof NodeList<?>) {
			NodeList<Node> list = (NodeList<Node>) parent;
			for(Node node: list) {
				if(node instanceof VariableAccess) {
					VariableAccess brother = (VariableAccess) node;
					if(brother.getName().equals(name)) {
						brother.setRef(this);
					}
				}
			}
		}
		
		int lineIndex = stack.find(Line.class);
		if(lineIndex == -1) {
			throw new OocCompilationError(this, stack, "Not in a line! How are we supposed to add one? Stack = "+stack.toString(true));
		}	
		Line line = (Line) stack.get(lineIndex);
		
		int bodyIndex = lineIndex - 1;
		if(bodyIndex == -1) {
			throw new OocCompilationError(this, stack, "Didn't find a nodelist containing the line! How are we suppoed to add one? Stack = "+stack.toString(true));
		}
		NodeList<Line> body = (NodeList<Line>) stack.get(bodyIndex);
		
		int declIndex = stack.find(VariableDecl.class);
		if(declIndex == -1) {
			Block block = new Block(startToken);
			block.getBody().add(new Line(this));
			block.getBody().add(line);
			body.replace(line, new Line(block));
		} else {
			body.addBefore(line, new Line(this));
		}
		return true;
		
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == expression) {
			expression = (Expression) kiddo;
			return true;
		}
		if(oldie == type) {
			type = (Type) kiddo;
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		
		//if(isConst) sb.append("const ");
		if(isStatic) sb.append("static ");
		sb.append(name);
		if(expression != null) {
			sb.append(" = ").append(expression);
		}
		sb.append(" : ");
		sb.append(type);
		return sb.toString();
	}

	public boolean shouldBeLowerCase() {
		return (externName == null || externName.length() > 0) && type != null && !(type.getName().equals("Class"));
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	public Expression getExpression() {
		return expression;
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		Response response = super.resolve(stack, res, fatal);
		if(response != Response.OK) return response;
		
		if(type == null) {
			if(expression != null) {
				type = expression.getType();
			}
			if(type == null && fatal) {
				throw new OocCompilationError(this, stack, "Couldn't infer type of variable "+getName());
			}
		}
		
		if(!isArg() && expression != null && expression.getType() != null && expression.getType().isGeneric()) {
			setExpression(new Cast(expression, getType(), expression.startToken));
		}
		for(int i = 0; i < RESERVED_NAMES.length; i++) {
			if(RESERVED_NAMES[i].equals(name)) {
			 throw new OocCompilationError(this, stack, "'"+name
				+"' is a reserved keyword in C99, you can't declare something with that name.");
			}
		}
		
		if(expression != null) {
            Expression realExpr = expression;
            Cast cast = null;
            while(realExpr instanceof Cast) {
            	cast = (Cast) realExpr;
                realExpr = cast.inner;
            }
            if(realExpr instanceof FunctionCall) {
                FunctionCall fCall = (FunctionCall) realExpr;
                FunctionDecl fDecl = fCall.getImpl();
                if(fDecl == null || !fDecl.getReturnType().isResolved()) {
                    // fCall isn't resolved
                    return Response.LOOP;
                }

                if(fDecl.getReturnType().isGeneric()) {
                	if(getType() == null) {
                		return Response.LOOP;
                	}
                	setType(getType()); // fixate the type
                	
                    Assignment ass = new Assignment(new VariableAccess(this, startToken), cast != null ? cast : realExpr, startToken);
                    stack.addAfterLine(stack, ass);
                    // token throwError("Couldn't add a " + ass toString() + " after a " + toString() + ", trail = " + trail toString())
                    setExpression(null);
                }
            }
        }
		
		if(type != null && type.isGeneric() && type.getPointerLevel() == 0) {
            if(expression != null) {
                if((expression instanceof FunctionCall) && ((FunctionCall) expression).getName().equals("gc_malloc")) {
                	return Response.OK;
                }
                
                Assignment ass = new Assignment(new VariableAccess(this, startToken), expression, startToken);
                addAfterLine(stack, ass);
                
                this.expression = null;
            }
            FunctionCall fCall = new FunctionCall("gc_malloc", startToken);
            VariableAccess tAccess = new VariableAccess(type.getName(), startToken);
            MemberAccess sizeAccess = new MemberAccess(tAccess, "size", startToken);
            fCall.getArguments().add(sizeAccess);
            setExpression(fCall);
            // just set expr to gc_malloc cause generic!
            return Response.LOOP;
        }
		
		return Response.OK;
	}
	
	public void setVersion(VersionBlock version) {
		this.version = version;
	}
	
	public VersionBlock getVersion() {
		return version;
	}
	
	@Override
	public void addToModule(Module module) {
		module.getBody().add(new Line(this));
	}
	
}

