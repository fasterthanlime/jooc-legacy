package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.types.TypeDef;
import org.ooc.nodes.types.TypeDefFunctionPointer;
import org.ooc.nodes.types.Type;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

class TypeDefParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException, SyntaxError {

		TypeDef typeDef = null;
		
		typeDef = read(context.reader);
		if(typeDef == null) {
			return false;
		}
		
		context.add(typeDef);
		return true;
		
	}

	public static TypeDef read(SourceReader reader) throws IOException, SyntaxError {

		if (reader.matches("typedef", true) && reader.hasWhitespace(true)) {
			
			//System.err.println(reader.getLocation()+" TypeDef: trying to read a type def...");
			
			Type typeUsage = null;
			
			reader.skipWhitespace();
	        typeUsage = Type.read(null, reader);
	        reader.skipWhitespace();

			TypeDef typeDef;
	        reader.skipWhitespace();
	        //System.err.println(reader.getLocation()+" TypeDef: got typeUsage "+typeUsage.name+", next is "+SourceReader.spelled(reader.peek()));
	        if(reader.matches("(", true)) {
	        
	        	//System.err.println(reader.getLocation()+" TypeDef: got opening paren");
	        	
	        	reader.skipWhitespace();
	        	// Gosh, function pointer typedef!
	        	if(!reader.matches("*", true)) {
//	        		System.err.println(reader.getLocation()+
//	        				": Unrecognized function pointer typedef! got the opening "
//	        				+"\nparenthesis, but not the '*', wtf?");
	        		return null;
	        	}
	        	
	        	//System.err.println(reader.getLocation()+" TypeDef: got star");
	        	
	        	reader.skipWhitespace();
	        	String name = reader.readName();
	        	if(name.isEmpty()) {
//	        		System.err.println(reader.getLocation()+
//	        				": Unrecognized function pointer typedef! got the opening "
//	        				+"\nparenthesis, the '*', but no name? wtf?");
	        		return null;
	        	}
	        	
	        	//System.err.println(reader.getLocation()+" TypeDef: got name "+name);
	        	
	        	reader.skipWhitespace();
	        	if(!reader.matches(")", true)) {
//	        		System.err.println(reader.getLocation()+
//	        				": Unrecognized function pointer typedef! got the opening "
//	        				+"\nparenthesis, the '*', the name, but no closing paren? wtf?");
	        		return null;
	        	}
	        	
	        	//System.err.println(reader.getLocation()+" TypeDef: got closing paren");
	        	
	        	reader.skipWhitespace();
	        	// Now read the args.. but won't be much use really
	        	String args = reader.readBlock('(', ')');
	        	
	        	typeDef = new TypeDefFunctionPointer(reader.getLocation(), typeUsage, name, args);
	        	
	        } else {
	        	
	        	// Simple typedef, just read the name
	        	String name = reader.readName();
	        	
	        	if(name.equals("enum")) {
	        		// Just give up for now..
	        		return null;
	        	}
	        	
	        	typeDef = new TypeDef(reader.getLocation(), typeUsage, name);	
	        
	        }
	        
	        reader.skipWhitespace();
	        if(!reader.matches(";", true)) {
	        	throw new SyntaxError(reader.getLocation(), "Missing semicolon after typedef.");
	        }
	        
	        return typeDef;

        }
		
		return null;
		
	}

}
