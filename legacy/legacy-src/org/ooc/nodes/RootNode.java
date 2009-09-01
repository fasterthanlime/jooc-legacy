package org.ooc.nodes;

import java.io.IOException;
import java.util.ArrayList;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * The root node is the father of all nodes.
 * 
 * @author Amos Wenger
 */
public class RootNode extends Scope {

	/** This function will be executed before anything in a module (e.g. .ooc source file) is used */
	public final FunctionDef loadModule;
	
	/** The source context this root node belongs to */
	public final SourceContext context;
	
	/**
	 * Default constructor
	 * @param location
	 */
	public RootNode(FileLocation location, String moduleUnderName, SourceContext context) {
		
		super(location);
		
		this.context = context;
		Function function = new Function("loadModule_"+moduleUnderName,
				Type.getVoid(), null, new TypedArgumentList(location, new ArrayList<Variable>()));
		loadModule = new FunctionDef(location, function);
		
	}
	
	
	@Override
	public void writeToCHeader(Appendable a) throws IOException {
	
		for(ClassDef def: getNodesTyped(ClassDef.class)) {
			def.writeForwardDef(a);
		}
		a.append('\n');
		
		for(SyntaxNode node: nodes) {
            node.writeToCHeader(a);
        }
		
		if(!loadModule.nodes.isEmpty()) {
			loadModule.writeToCHeader(a);
		}
		
		a.append('\n'); // Old compilers complain without a newline at the end of the file
		
	}
	
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {

		for(SyntaxNode node: nodes) {
            node.writeToCSource(a);
        }
		
		if(!loadModule.nodes.isEmpty()) {
			a.append('\n');
			loadModule.writeToCSource(a);
		}
		
		a.append('\n'); // Old compilers complain without a newline at the end of the file
		
	}
	
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		loadModule.function.returnType.assemble(manager);
		lock();
		
	}
	
	
	@Override
	protected boolean isIndented() {
		
		return false;
		
	}
	
	/**
	 * Add a node to the loadModule() routine
	 * @param node
	 */
	public void addModuleInitialization(SyntaxNode node) {
		
		loadModule.add(node);
		
	}
	
	
	@Override
	public RootNode getRoot() {

		return this;

	}

}
