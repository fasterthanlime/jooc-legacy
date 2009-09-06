package org.ooc.frontend.model;

import java.io.IOException;
import java.util.Iterator;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;

public class OpDecl extends Declaration {

	public enum OpType {
		ADD,
		ADD_ASS,
		SUB,
		SUB_ASS,
		MUL,
		MUL_ASS,
		DIV,
		DIV_ASS,
		ASS,
		MOD,
		L_OR,
		L_AND,
		B_OR,
		B_AND,
		IDX,
		IDX_ASS,
		GT,
		GTE,
		LT,
		LTE,
		EQ,
		NE;
		
		public String toPrettyString() {
			switch(this) {
			case ADD:
				return "+";
			case DIV:
				return "/";
			case IDX_ASS:
				return "[]=";
			case IDX:
				return "[]";
			case MUL:
				return "*";
			case SUB:
				return "-";
			case B_AND:
				return "&";
			case B_OR:
				return "|";
			case L_AND:
				return "&&";
			case L_OR:
				return "||";
			case MOD:
				return "%";
			case EQ:
				return "==";
			case GT:
				return ">";
			case GTE:
				return ">=";
			case LT:
				return "<";
			case LTE:
				return "<=";
			case NE:
				return "!=";
			case ASS:
				return "=";
			case ADD_ASS:
				return "+=";
			case DIV_ASS:
				return "/=";
			case MUL_ASS:
				return "*=";
			case SUB_ASS:
				return "-=";
			default:
				return "unknown";
			}
		}

		public boolean isNumeric() {
			switch(this) {
			case ADD: case SUB: case MUL: case DIV:
			case ADD_ASS: case SUB_ASS: case MUL_ASS: case DIV_ASS:
				return true;
			default:
				return false;
			}
		}
	}
	
	protected OpType opType;
	protected FunctionDecl func;
	
	public OpDecl(OpType opType, FunctionDecl func, Token startToken) {
		super("Operator "+opType, startToken);
		this.opType = opType;
		this.func = func;
		String name = "__OP_"+opType.toString();
		Iterator<Argument> iter = func.getArguments().iterator();
		while(iter.hasNext()) {
			name += "_" + iter.next().getType().getMangledName();
		}
		func.setName(name);
	}

	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == func) {
			func = (FunctionDecl) kiddo;
			return true;
		}
		
		return false;
	}

	@Override
	public Type getType() {
		return new Type("Operator", Token.defaultToken);
	}
	
	public OpType getOpType() {
		return opType;
	}
	
	public FunctionDecl getFunc() {
		return func;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		func.accept(visitor);
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	public String getOpString() {
		return opType.toPrettyString();
	}
	
	@Override
	public String toString() {
		return "operator "+getOpString()+" "+func.getArgsRepr();
	}
	
	@Override
	public TypeDecl getTypeDecl() {
		throw new Error("getting type decl of an "+getClass().getSimpleName()+", wtf?");
	}

}
