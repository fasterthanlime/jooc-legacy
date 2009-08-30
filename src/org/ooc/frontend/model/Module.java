package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;
import org.ubi.SourceReader;

public class Module extends Node implements Scope {

	protected String underName;
	protected String fullName;
	protected String name;
	protected NodeList<Include> includes;
	protected NodeList<Import> imports;
	protected NodeList<Use> uses;
	protected MultiMap<String, TypeDecl> types;
	protected MultiMap<String, FunctionDecl> functions;
	protected NodeList<OpDecl> ops;
	protected NodeList<Node> body;
	protected String fileName;
	protected FunctionDecl loadFunc;
	private boolean isMain;
	private final transient SourceReader reader;
	
	public long lastModified;
	
	public Module(String fullName, SourceReader reader) {
		
		super(Token.defaultToken);
		this.reader = reader;
		
		this.fullName = fullName; // just to make sure
		this.fileName = fullName.replace('.', File.separatorChar);
		int index = fullName.lastIndexOf('.');
		if(index == -1) name = fullName;
		else name = fullName.substring(index + 1);
		this.underName = "_"+fullName.replaceAll("[^a-zA-Z0-9_]", "_");
		
		this.includes = new NodeList<Include>(startToken);
		this.imports = new NodeList<Import>(startToken);
		this.uses = new NodeList<Use>(startToken);
		this.body = new NodeList<Node>(startToken);
		this.ops = new NodeList<OpDecl>();
		this.types = new MultiMap<String, TypeDecl>();
		this.functions = new MultiMap<String, FunctionDecl>();
		
		// set it as extern, so it won't get written implicitly
		this.loadFunc = new FunctionDecl(underName + "_load", "", false, false, false, true, Token.defaultToken);
		
	}
	
	public String getSimpleName() {
		return name;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getPath() {
		return getPath(".ooc");
	}

	public String getPath(String extension) {
		return getFileName() + extension;
	}

	public NodeList<Include> getIncludes() {
		return includes;
	}
	
	public NodeList<Import> getImports() {
		return imports;
	}
	
	public NodeList<Use> getUses() {
		return uses;
	}
	
	public NodeList<Node> getBody() {
		return body;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {
		includes.accept(visitor);
		imports.accept(visitor);
		uses.accept(visitor);
		body.accept(visitor);
		types.accept(visitor);
		ops.accept(visitor);
		loadFunc.accept(visitor);
	}
	
	public <T extends Declaration> MultiMap<Node, T> getDeclarationsMap(final Class<T> clazz) throws IOException {

		final MultiMap<Node, T> decls = new MultiMap<Node, T>();
		this.getDeclarationsMap(clazz, decls);
		for(Import imp: imports) {
			imp.getModule().getDeclarationsMap(clazz, decls);
		}
		return decls;
		
	}

	protected <T extends Declaration> void getDeclarationsMap(final Class<T> clazz,
			final MultiMap<Node, T> decls) throws IOException {
		
		new Nosy<T> (clazz, new Opportunist<T>() {
	
			@Override
			public boolean take(T node, NodeList<Node> stack) throws IOException {
				
				int index = stack.find(Scope.class);
				if(index == -1) {
					throw new Error("Found declaration "+node.getName()+" of type "
							+node.getType()+" outside of any NodeList!");
				}
				decls.add(stack.get(index), clazz.cast(node));
				return true;
				
			}
			
		}).visit(this);
		
	}
	
	public <T extends Declaration> NodeList<T> getDeclarationsList(final Class<T> clazz) throws IOException {

		final NodeList<T> decls = new NodeList<T>();
		this.getDeclarationsList(clazz, decls);
		for(Import imp: imports) {
			imp.getModule().getDeclarationsList(clazz, decls);
		}
		return decls;
		
	}

	protected <T extends Declaration> void getDeclarationsList(final Class<T> clazz,
			final NodeList<T> decls) throws IOException {
		new Nosy<T> (clazz, new Opportunist<T>() {
	
			@Override
			public boolean take(T node, NodeList<Node> stack) throws IOException {
				
				int index = stack.find(Scope.class);
				if(index == -1) {
					throw new Error("Found declaration "+node.getName()+" of type "
							+node.getType()+" outside of any NodeList!");
				}
				decls.add(clazz.cast(node));
				return true;
				
			}
			
		}).visit(this);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString()+" : "+name;
	}
	
	public String getUnderName() {
		return underName;
	}

	public String getLoadFuncName() {
		return loadFunc.getName();
	}
	
	public FunctionDecl getLoadFunc() {
		return loadFunc;
	}

	public boolean isMain() {
		return isMain;
	}
	
	public void setMain(boolean isMain) {
		this.isMain = isMain;
	}

	public SourceReader getReader() {
		return reader;
	}

	@Override
	public VariableDecl getVariable(String name) {
		for(Node node: body) {
			if(node instanceof Line) {
				node = ((Line) node).getStatement();
			}
			if(node instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl) node;
				if(varDecl.getName().equals(name)) return varDecl;
			}
		}
		return null;
	}

	@Override
	public void getVariables(NodeList<VariableDecl> variables) {
		for(Node node: body) {
			if(node instanceof VariableDecl) {
				variables.add((VariableDecl) node);
			}
		}
	}

	@Override
	public FunctionDecl getFunction(String name, FunctionCall call) {
		for(Node node: body) {
			if(node instanceof FunctionDecl) {
				FunctionDecl func = (FunctionDecl) node;
				if(func.getName().equals(name)
						&& (call == null || call.matches(func))) return func;
			}
		}
		return null;
	}

	@Override
	public void getFunctions(NodeList<FunctionDecl> functions) {
		for(Node node: body) {
			if(node instanceof FunctionDecl) {
				functions.add((FunctionDecl) node);
			}
		}
	}
	
	public MultiMap<String, TypeDecl> getTypes() {
		return types;
	}
	
	public NodeList<OpDecl> getOps() {
		return ops;
	}

	public TypeDecl getType(String typeName) {
		TypeDecl typeDecl = getTypes().get(typeName);
		if(typeDecl != null) return typeDecl;
		for(Import imp: imports) {
			typeDecl = imp.getModule().getTypes().get(typeName);
			if(typeDecl != null) return typeDecl;
		}
		return null;
	}
	
}
