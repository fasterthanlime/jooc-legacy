package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Function;
import org.ubi.FileLocation;

/**
 * A static block is a block executed at the first instanciation of a class.
 * 
 * @author Amos Wenger
 */
public class StaticBlock extends FunctionDef {

	/**
	 * Default constructor
	 * @param location
	 */
	public StaticBlock(FileLocation location) {

		super(location, new Function("_staticBlock", Type.VOID, null, new TypedArgumentList(location)));
		
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
		
		// it's written in main.
		
	}
	
}
