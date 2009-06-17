package org.ooc.nodes.clazz;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.structures.Clazz;
import org.ubi.FileLocation;

/**
 * A cover is a kind of "fake class" which allows to add functions to
 * base types.
 * 
 * @author Amos Wenger
 */
public class Cover extends ClassDef {

	/**
	 * Create a new cover
	 * @param location
	 * @param clazz
	 */
	public Cover(FileLocation location, Clazz clazz) {
		
		super(location, clazz);
		clazz.isCover = true;
		clazz.getType().isCover = true;
		
	}
	
	@Override
	public void writeForwardDef(Appendable a) throws IOException {
	
		// Doodelidoo
		
	}
	
	@Override
	public void writeToCHeader(Appendable a) throws IOException {
	
		writeWhitespace(a);
		a.append("\n// Cover definition of '"+clazz.fullName+"'\n");
		
		for(SyntaxNode node: nodes) {
			if(node instanceof FunctionDef) {
				FunctionDef def = (FunctionDef) node;
				def.function.writePrototype(a, clazz);
				a.append(";\n");
			}
		}
		
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		for(SyntaxNode node: nodes) {
			node.writeToCSource(a);
		}
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		// Sit back, relax
		
	}

}
