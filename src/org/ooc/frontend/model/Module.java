package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.structs.MultiMap;
import org.ubi.SourceReader;

public class Module extends Node implements Scope {

	protected String underName;
	protected String fullName;
	protected String name;
	protected String memberPrefix;
	protected NodeList<Include> includes;
	protected NodeList<Import> globalImports;
	protected NodeList<NamespaceDecl> namespaces;
	protected NodeList<Use> uses;
	protected MultiMap<String, TypeDecl> types;
	//protected MultiMap<String, FunctionDecl> functions;
	protected NodeList<OpDecl> ops;
	protected NodeList<Node> body;
	protected String fileName;
	protected FunctionDecl loadFunc;
	private boolean isMain;
	private final transient SourceReader reader;
	
	public NodeList<Node> parseStack = new NodeList<Node>();
	
	public long lastModified;
	private String packageName;
	private String pathElement;

	public Module(String fullName, File element, SourceReader reader) {
		
		super(Token.defaultToken);
		this.reader = reader;
		try {
			this.pathElement = element.getCanonicalFile().getName().replace(".", "_");
		} catch (IOException e) {
			this.pathElement = element.getName().replaceAll(".", "_");
		}
		
		this.fullName = fullName; // just to make sure
		this.fileName = fullName.replace('.', File.separatorChar);
		int index = fullName.lastIndexOf('.');
		if(index == -1) {
			name = fullName;
			packageName = "";
		} else{
			name = fullName.substring(index + 1);
			packageName = fullName.substring(0, index).replaceAll("[^a-zA-Z0-9_]", "_");
		}
		
		this.underName = "_"+fullName.replaceAll("[^a-zA-Z0-9_]", "_");
		this.memberPrefix = fullName.replaceAll("[^a-zA-Z0-9_]", "_") + "__";
		if(!this.memberPrefix.matches("^[a-zA-Z_].*$")) {
			// the first char has to be [a-zA-Z_].
			this.memberPrefix = "_" + this.memberPrefix;
		}

		this.includes = new NodeList<Include>(startToken);
		this.globalImports = new NodeList<Import>(startToken);
		this.namespaces = new NodeList<NamespaceDecl>(startToken);
		this.uses = new NodeList<Use>(startToken);
		this.body = new NodeList<Node>(startToken);
		this.ops = new NodeList<OpDecl>();
		this.types = new MultiMap<String, TypeDecl>();
		//this.functions = new MultiMap<String, FunctionDecl>();
		
		// set it as extern, so it won't get written implicitly
		this.loadFunc = new FunctionDecl(underName + "_load", "", false, false, false, true, Token.defaultToken, this);
		
	}

	public void addNamespace(NamespaceDecl decl) {
		namespaces.add(decl);
	}

	public NamespaceDecl getNamespace(String name) {
		for(NamespaceDecl decl: namespaces) {
			if(decl.getName().equals(name)) {
				return decl;
			}
		}
		return null;
	}

	public String getMemberPrefix() {
		return memberPrefix;
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
	
	public String getParentPath() {
		return new File(getFileName()).getParent();
	}
	
	public String getPath() {
		return getPath(".ooc");
	}

	public String getPath(String extension) {
		return getOutPath() + extension;
	}
	
	public String getOutPath() {
		return getOutPath(File.separatorChar);
	}
	
	public String getOutPath(char separatorChar) {
		String outPath = pathElement+"/"+fullName.replace('.', separatorChar);
		return outPath;
	}

	public String getPrefixLessPath() {
		return fullName.replace(".", File.separator) + ".ooc";
	}

	public NodeList<Include> getIncludes() {
		return includes;
	}
	
	public NodeList<Import> getGlobalImports() {
		return globalImports;
	}

	public NodeList<Import> getAllImports() {
		if(namespaces.isEmpty()) return globalImports;

		NodeList<Import> imports = new NodeList<Import>();
		imports.addAll(getGlobalImports());
		for(NamespaceDecl ns: namespaces)
			imports.addAll(ns.getImports());
		return imports;
	}
	
	public NodeList<Use> getUses() {
		return uses;
	}
	
	public NodeList<Node> getBody() {
		return body;
	}

	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	public boolean hasChildren() {
		return true;
	}

	public void acceptChildren(Visitor visitor) throws IOException {
		includes.accept(visitor);
		globalImports.accept(visitor);
		uses.accept(visitor);
		body.accept(visitor);
		types.accept(visitor);
		ops.accept(visitor);
		loadFunc.accept(visitor);
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

	public VariableDecl getVariable(String name) {
		VariableDecl varDecl = getVariableInBody(name, body);
		if (varDecl != null) return varDecl;
		
		for(Import imp: globalImports) {
			varDecl = getVariableInBody(name, imp.getModule().body);
			if (varDecl != null) return varDecl;
		}
		return null;
	}

	private VariableDecl getVariableInBody(String name, NodeList<?> list) {
		for(Node node: list) {
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

	public void getVariables(NodeList<VariableDecl> variables) {
		for(Node node: body) {
			if(node instanceof VariableDecl) {
				variables.add((VariableDecl) node);
			} else if(node instanceof Line && ((Line)node).statement instanceof VariableDecl) {
				variables.add((VariableDecl)((Line)node).statement);
			}
		}
	}

	public FunctionDecl getFunction(String name, String suffix, FunctionCall call) {
		return getFunction(name, suffix, call, 0, null);
	}
	
	public FunctionDecl getFunction(String name, String suffix, FunctionCall call,
			int bestScoreParam, FunctionDecl bestMatchParam) {
		int bestScore = bestScoreParam;
		FunctionDecl bestMatch = bestMatchParam;
		for(Node node: body) {
			if(node instanceof FunctionDecl) {
				FunctionDecl function = (FunctionDecl) node;
				if(function.isNamed(name, suffix)) { 
					if (call == null) return function;
					int score = call.getScore(function);
					if(score > bestScore) {
						bestScore = score;
						bestMatch = function;
					}
				}
			}
		}
		return bestMatch;
	}

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
	
	public void addType(TypeDecl tDecl) {
		if(tDecl.getVersion() != null) {
			//System.out.println("tDecl " + tDecl + " is versioned " + tDecl.getVersion());
			
			/**
			 * When we're going to want cross-compilation, we'll want to make that
			 * *not* Target.guessHost()
			 */
			types.put(tDecl.getName(), tDecl);
		}
	}
	
	public NodeList<OpDecl> getOps() {
		return ops;
	}

	public TypeDecl getType(String typeName) {
		// FIXME make this version-aware
		TypeDecl typeDecl = getTypes().get(typeName);
		if(typeDecl != null) return typeDecl;
		
		for(Import imp: globalImports) {
			Module module = imp.getModule();
			if(module != null) {
				typeDecl = module.getTypes().get(typeName);
				if(typeDecl != null) return typeDecl;
			}
		}
		
		return null;
	}

	public String getPackageName() {
		return packageName;
	}
	
}
