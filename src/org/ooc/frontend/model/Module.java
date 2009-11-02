package org.ooc.frontend.model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.structs.NodeMap;
import org.ubi.SourceReader;

public class Module extends Node implements Scope {

	protected String underName;
	protected String fullName;
	protected String name;
	protected NodeList<Include> includes;
	protected NodeList<Import> imports;
	protected NodeList<Use> uses;
	protected NodeMap<String, TypeDecl> types;
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
		
		this.includes = new NodeList<Include>(startToken);
		this.imports = new NodeList<Import>(startToken);
		this.uses = new NodeList<Use>(startToken);
		this.body = new NodeList<Node>(startToken);
		this.ops = new NodeList<OpDecl>();
		this.types = new NodeMap<String, TypeDecl>(new LinkedHashMap<String, TypeDecl>());
		//this.functions = new MultiMap<String, FunctionDecl>();
		
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
	
	public NodeList<Import> getImports() {
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
		imports.accept(visitor);
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
		for(Import imp: imports) {
			varDecl = getVariableInBody(name, imp.getModule().body);
			if (varDecl != null) return varDecl;
		}
		return null;
	}

	private VariableDecl getVariableInBody(String name, NodeList<Node> list) {
		for(Node node: list) {
			if(node instanceof Line) {
				node = ((Line) node).getStatement();
			}
			if(node instanceof VariableDecl) {
				VariableDecl varDecl = (VariableDecl) node;
				if(varDecl.hasAtom(name)) return varDecl;
			}
		}
		return null;
	}

	public void getVariables(NodeList<VariableDecl> variables) {
		for(Node node: body) {
			if(node instanceof VariableDecl) {
				variables.add((VariableDecl) node);
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
	
	public Map<String, TypeDecl> getTypes() {
		return types;
	}
	
	public NodeList<OpDecl> getOps() {
		return ops;
	}

	public TypeDecl getType(String typeName) {
		TypeDecl typeDecl = getTypes().get(typeName);
		if(typeDecl != null) return typeDecl;
		for(Import imp: imports) {
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
