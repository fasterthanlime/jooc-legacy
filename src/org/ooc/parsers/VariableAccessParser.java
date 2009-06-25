package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.others.Name;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

/**
 * Represents access to a variable. The variable must have been
 * declared before, e.g.
 * 
 * @author Amos Wenger
 */
public class VariableAccessParser implements Parser {

	
	public boolean parse(SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		boolean result = false;
		
		FileLocation location = reader.getLocation();
		String name = reader.readName();

		ClassDef classDef = context.getNearest(ClassDef.class);
		reader.skipWhitespace();
		if(classDef != null) {
			Variable member = classDef.getMember(context, name);
			if(member != null) {
				context.add(new Name(location, name));
				result = true;
			}
		}
		
		if(!result) {
			Scope scope = context.getNearest(Scope.class);
			if(scope != null) {
				Variable variable = scope.getVariable(name);
				if(variable != null) {
					context.add(new Name(location, name));
					result = true;
				}
			}
		}
		
		return result;
		
	}

}
