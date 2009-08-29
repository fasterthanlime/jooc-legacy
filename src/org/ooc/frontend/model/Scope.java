package org.ooc.frontend.model;


/**
 * This interface must be implemented by everything which is capable
 * of holding variable declarations, ie. a "scope" in the C meaning of the term.
 * 
 * Examples: SourceUnit, ClassDecl, FunctionDecl...
 * 
 * @author Amos Wenger
 */
public interface Scope {

	public VariableDecl getVariable(String name);
	public void getVariables(NodeList<VariableDecl> variables);
	public FunctionDecl getFunction(String name, FunctionCall call);
	public void getFunctions(NodeList<FunctionDecl> functions);
	
}
