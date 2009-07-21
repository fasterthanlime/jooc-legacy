package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.keywords.AbstractKeyword;
import org.ooc.nodes.keywords.ConstKeyword;
import org.ooc.nodes.keywords.ReverseKeyword;
import org.ooc.nodes.keywords.StaticKeyword;
import org.ooc.nodes.keywords.UnmangledKeyword;
import org.ooc.nodes.others.RawCode;
import org.ubi.CompilationFailedError;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

class KeywordsParser implements Parser {

	
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		FileLocation location = reader.getLocation();
		
		boolean success;
		
        if (reader.matches("abstract", true) && reader.hasWhitespace(true)) {

            context.add(new AbstractKeyword(location));
            success = true;

        } else if (reader.matches("callback", true) && reader.hasWhitespace(true)) {

            throw new CompilationFailedError(reader.getLocation(), "The 'callback' keyword is deprecated. It's a dangerous hack " +
            		"and alternative solutions should be used. Ideally this would be handled with a just-in-time assembler");

        } else if (reader.matches("unmangled", true) && reader.hasWhitespace(true)) {

        	context.add(new UnmangledKeyword(location));
            success = true;

        } else if (reader.matches("inline", true) && reader.hasWhitespace(true)) {

        	context.add(new RawCode(location, "inline "));
            success = true;

        } else if (reader.matches("const", true) && reader.hasWhitespace(true)) {

			context.add(new ConstKeyword(location));
            success = true;

        } else if (reader.matches("static", true) && reader.hasWhitespace(true)) {

        	context.add(new StaticKeyword(location));
            success = true;

        } else if (reader.matches("reverse", true) && reader.hasWhitespace(true)) {

			context.add(new ReverseKeyword(location));
            success = true;

        } else {
        	
        	success = false;
        	
        }
        
        return success;
		
	}

}
