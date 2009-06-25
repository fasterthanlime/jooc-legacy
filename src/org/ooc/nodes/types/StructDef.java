package org.ooc.nodes.types;

import java.io.IOException;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 * A structure definition 
 * 
 * @author Amos Wenger
 */
public class StructDef extends SyntaxNode {

	private String name;
	private String block;
	
	/**
	 * Default constructor
	 * @param location
	 * @param name
	 * @param block
	 */
	public StructDef(FileLocation location, String name, String block) {
		super(location);
		this.name = name;
		this.block = block;
	}

	
	public void writeToCSource(Appendable a) throws IOException {
		// Yeah. No. Forget it.
	}
	
	
	@Override
	public void writeToCHeader(Appendable a) throws IOException {
		writeWhitespace(a);
		a.append("struct ");
		a.append(name);
		a.append(" {");
		a.append(block);
		a.append("};\n\n");
	}

}
