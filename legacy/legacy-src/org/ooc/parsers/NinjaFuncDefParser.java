package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Parse the new ninja syntax of function definitions 
 * 
 * @author Amos Wenger
 */
public class NinjaFuncDefParser implements Parser {

	
	public boolean parse(SourceContext context) throws IOException, SyntaxError {

		SourceReader reader = context.reader;
		
		if(reader.matches("func", true) && reader.hasWhitespace(true)) {
			
			ClassDef classDef = context.getNearest(ClassDef.class);
			
			Type returnType = Type.VOID;
			TypedArgumentList args;
			String name = reader.readName();
			
			if(name.isEmpty()) {
				reader.err("Expected function name after 'func' keyword.");
			}
			
			if(name.equals("new")) {
				if(classDef == null) {
					reader.err("Constructor (new()) outside Class Definition. Sucks to be you :/");
				} else {
					returnType = classDef.clazz.getType();
				}
			}
			
			reader.skipWhitespace();
			
			if(reader.matches("(", false)) {
				args = TypedArgumentList.read(context);
				reader.skipWhitespace();
			} else {
				args = new TypedArgumentList(reader.getLocation());
			}
			
			if(reader.matches("=", true) && reader.hasWhitespace(true)) {
				reader.err("Deprecated syntax '=' to specify the return type of a function. Use '->' instead.");
			}
			
			if(reader.matches("->", true) && reader.hasWhitespace(true)) {
				if(name.equals("new")) {
					reader.err("Don't specify the return type of 'new'. I (the compiler) am not so dumb.");
				}
				reader.skipWhitespace();
				returnType = Type.read(context, reader);
				if(returnType == null) {
					reader.err("Expected return type of function after '->'");
				}
				reader.skipWhitespace();
			}
			
			if(classDef != null) {
				Variable member = classDef.getMember(context, name);
				if(member != null) {
					returnType = member.type; // We successfully inferred the function's return type! Nice =)
				}
			}
			reader.skipWhitespace();
			
			return FunctionDefParser.parseFuncArgs(context, reader, classDef, args, returnType, null, name);
			
		}
		
		return false;
		
	}

}
