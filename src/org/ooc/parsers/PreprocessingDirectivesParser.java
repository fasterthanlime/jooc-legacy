package org.ooc.parsers;

import java.io.IOException;

import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.libs.Include;
import org.ooc.nodes.libs.Include.IncludePosition;
import org.ooc.nodes.libs.Include.IncludeType;
import org.ooc.nodes.preprocessor.Define;
import org.ooc.nodes.preprocessor.Macro;
import org.ooc.nodes.preprocessor.RawPreprocessorDirective;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

class PreprocessingDirectivesParser implements Parser {

	
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		if (reader.backMatches('\n', true) && reader.matches("#", true)) {
			
				FileLocation location = reader.getLocation();
		        reader.skipWhitespace();
		        
		        int mark = reader.mark();
				if(reader.matches("include", true) && reader.hasWhitespace(true)) {
					
					try {
						if(reader.matches("<", false)) {
							context.add(new Include(location, IncludeType.PATHBOUND, IncludePosition.HEADER, reader.readBlock('<', '>')));
							return true;
						} else if(reader.matches("\"", false)) {
							context.add(new Include(location, IncludeType.LOCAL, IncludePosition.HEADER, reader.readBlock('"', '"')));
							return true;
						} else {
							context.err("Expected <...> or \"...\", enclosing an include path.");
						}
					} catch(Exception e) {
						throw new CompilationFailedError(e);
					}
					
				}
				
				reader.reset(mark);
				if(reader.matches("define", true) && reader.hasWhitespace(true)) {
					
					String name = reader.readName();
					
					if(reader.matches("(", true)) {
						String args = reader.readUntil(')', false);
						reader.matches(")", true);
						String content = reader.readLine();
						context.add(new Macro(location, name, args, content));
					} else {
						String content = reader.readLine();
						context.add(new Define(location, name, content));
					}
					
					return true;
					
				}
				
				reader.reset(mark);
				if(reader.matches("!", true)) {
					
					// Whoops, she-bang!
					reader.readLine();
					return true;
					
				}
				
				context.add(new RawPreprocessorDirective(reader.getLocation(), reader.readLine()));
				return true;
	        
	    }

		return false;
		
	}

}
