package org.ooc.nodes.others;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.nodes.control.Scope;
import org.ooc.parsers.EnumParser;
import org.ubi.FileLocation;

/**
 * A simple enum.
 * @see EnumParser
 * 
 * @author Amos Wenger
 */
public class EnumNode extends Scope {

	String name;
	List<String> identifiers;

	/**
	 * Default constructor
	 * @param location
	 */
	public EnumNode(FileLocation location, String name) {
		super(location);
		this.name = name;
		this.identifiers = new ArrayList<String>();
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		boolean shouldBeComma = false;
		
		for(SyntaxNode node: nodes) {
			if(node instanceof Comma) {
				if(!shouldBeComma) {
					manager.errAndFail("Expected an enum identifier, not a comma", node);
				}
			} else if(node instanceof Name) {
				if(shouldBeComma) {
					manager.errAndFail("Expected a comma, not an enum identifier", node);
				}
				identifiers.add(((Name) node).content);
			}
			shouldBeComma = !shouldBeComma;
		}
		
		SyntaxNode next = getNext();
		if(next instanceof LineSeparator) {
			manager.warn("A semi-colon after an enum is unnecessary in ooc..", next);
			next.drop();
		}
		
	}
	
	@Override
	public void writeToCHeader(Appendable a) throws IOException {
	
		writeWhitespace(a);
		a.append("typedef enum ");
		a.append(name);
		a.append(" {\n");
		for(String identifier: identifiers) {
			writeWhitespace(a);
			a.append('\t');
			a.append(identifier);
			a.append(",\n");
		}
		writeWhitespace(a);
		a.append("} ");
		a.append(name);
		a.append(";\n");
		
	}
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
	
		// Sit in a rocking chair, take our head into one of our hands and nod
		// deeply while figuring out the meaning of life, the universe, and..EOF
		
	}
	
	@Override
	protected boolean isSpaced() {
		return false;
	}

	/**
	 * Test this enum's name for equality
	 * @param name a name in the form 'enum MyEnum' or 'MyEnum'
	 * @return true if this enum is named 'name' or not
	 */
	public boolean isNamed(String name) {

		return name.equals(this.name) || name.equals("enum "+this.name);
		
	}

}
