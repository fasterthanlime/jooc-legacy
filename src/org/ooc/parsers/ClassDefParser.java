package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.Cover;
import org.ooc.nodes.functions.FunctionImplementation;
import org.ooc.nodes.functions.FunctionOverride;
import org.ooc.nodes.keywords.FromKeyword;
import org.ooc.structures.Clazz;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

class ClassDefParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		boolean result = false;
		
		if (reader.matches("class", true) && reader.hasWhitespace(true)) {
        	if(context.isIn(ClassDef.class, false)) {
        		context.err("At "+reader.getLocation()+", Illegal class definition of "+reader.readName()+" inside class "+
        				context.getNearest(ClassDef.class).clazz.fullName+" (missing closing bracket '}' ?)");
        	}
        	try {
				context.open(readClassDef(context, reader));
				result = true;
        	} catch (SyntaxError e) {
				result = false;
			}
        } else if (reader.matches("cover", true) && reader.hasWhitespace(true)) {
        	if(context.isIn(ClassDef.class, false)) {
        		context.err("At "+reader.getLocation()+", Illegal cover definition of "+reader.readName()+" inside class "+
        				context.getNearest(ClassDef.class).clazz.fullName+" (missing closing bracket '}' ?)");
        	}
        	try {
				context.open(readCover(context, reader));
				result = true;
        	} catch (SyntaxError e) {
				result = false;
			}
        }
		
		if(!result && context.isIn(ClassDef.class, false)) {
			
			result = (overrideMatches(context) || implementMatches(context));
			
        }
		
		return result;
		
	}
	
	private Cover readCover(SourceContext context, SourceReader sourceReader) throws IOException, SyntaxError {
		
		sourceReader.skipWhitespace();
	    String name = sourceReader.readName();
	
	    sourceReader.skipWhitespace();
	    if(!sourceReader.matches("{", true)) {
	        sourceReader.err("A cover definition should be followed by a scope {}");
	    }
	
	    return new Cover(sourceReader.getLocation(), new Clazz(
	    		context.source.getInfo().getFullName(name), ""));
		
	}

	private ClassDef readClassDef(SourceContext context, SourceReader sourceReader) throws IOException, SyntaxError {

	    sourceReader.skipWhitespace();
	    String name = sourceReader.readName();
	
	    String zuper = "";
	    sourceReader.skipWhitespace();
	    FromKeyword from = FromKeyword.tryToRead(sourceReader);
	    if(from != null) {
	        zuper = from.getSuperClass();
	    }
	
	    sourceReader.skipWhitespace();
	    if(!sourceReader.matches("{", true)) {
	        sourceReader.err("A class definition should be followed by a scope {}");
	    }
	
	    return new ClassDef(sourceReader.getLocation(), new Clazz(context.source.getInfo().getFullName(name), zuper));
		
	}

	private boolean overrideMatches(SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		int mark = reader.mark();
		boolean result;
		
		if (reader.matches("override", true) && reader.hasWhitespace(true)) {

            reader.skipWhitespace();
            String name = reader.readName();
            reader.skipWhitespace();
            if(!reader.matches("{", true) && reader.hasWhitespace(true)) {
                context.err("Expected a block {} after 'override function'!");
                return false;
            }
            context.open(new FunctionOverride(reader.getLocation(), name));
            result = true;

        } else {
     
        	reader.reset(mark);
        	result = false;
        	
        }
		
		return result;
		
	}

	private boolean implementMatches(SourceContext context) throws IOException {
		
		SourceReader reader = context.reader;

		int mark = reader.mark();
		boolean result;
		
        if (reader.matches("implement", true) && reader.hasWhitespace(true)) {

            reader.skipWhitespace();
            String name = reader.readName();
            reader.skipWhitespace();
            if(!reader.matches("{", true) && reader.hasWhitespace(true)) {
                context.err("Expected scope after 'implement' keyword.");
                return false;
            }
            context.open(new FunctionImplementation(reader.getLocation(), name));
            result = true;

        } else {
        	
    		reader.reset(mark);
    		result = false;
        	
        }
        
        return result;
		
	}

}
