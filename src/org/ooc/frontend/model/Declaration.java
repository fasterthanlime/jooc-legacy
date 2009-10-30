package org.ooc.frontend.model;

import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public abstract class Declaration extends Expression implements MustBeResolved {

	protected String name;
	protected String externName;
	
	public static final String[] RESERVED_NAMES = new String[] {
		"auto",
		"break",
		"case",
		"char",
		"const",
		"continue",
		"default",
		"do",
		"double",
		"else",
		"enum",
		"extern",
		"float",
		"for",
		"goto",
		"if",
		"int",
		"long",
		"register",
		"return",
		"short",
		"signed",
		//"sizeof",
		"static",
		"struct",
		"switch",
		"typedef",
		"union",
		"unsigned",
		"void",
		"volatile",
		"while", 
		"inline",
		"_Imaginary",
		"_Complex",
		"_Bool",
		"restrict", 
	};
	
	public Declaration(String name, Token startToken) {
		this(name, null, startToken);
	}
	
	public Declaration(String name, String externName, Token startToken) {
		super(startToken);
		this.name = name;
		this.externName = externName;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract TypeDecl getTypeDecl();
	
	public boolean isExtern() {
		return externName != null;
	}
	
	public String getExternName() {
		if(externName == null || externName.length() == 0) return getName();
		return externName;
	}
	
	public String getExternName(VariableAccess variableAccess) {
		if(externName == null || externName.length() == 0) return variableAccess.getName();
		return externName;
	}
	
	public void setExternName(String externName) {
		this.externName = externName;
	}
	
	public boolean isExternWithName() {
		return externName != null && externName.length() > 0;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + name;
	}
	
	public boolean isResolved() {
		return false;
	}
	
	public Response resolve(NodeList<Node> stack, Resolver res, boolean fatal) {
		
		if(name != null) {
			for(int i = 0; i < RESERVED_NAMES.length; i++) {
				if(RESERVED_NAMES[i].equals(name)) {
				 throw new OocCompilationError(this, stack, "'"+name
					+"' is a reserved keyword in C99, you can't declare something with that name.");
				}
			}
		}
		
		return Response.OK;
		
	}
	
}
