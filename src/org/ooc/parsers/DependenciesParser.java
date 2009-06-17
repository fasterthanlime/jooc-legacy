package org.ooc.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import org.ooc.compiler.Compiler;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.libs.Include;
import org.ooc.nodes.libs.Use;
import org.ooc.nodes.libs.Include.IncludeLanguage;
import org.ooc.nodes.libs.Include.IncludePosition;
import org.ooc.nodes.libs.Include.IncludeType;
import org.ubi.SourceReader;

class DependenciesParser implements Parser {

	@Override
	public boolean parse(final SourceContext context) throws IOException {

		SourceReader reader = context.reader;
		
		if(!context.isInRoot()) {
			return false;
		}
		
		int mark = reader.mark();
        if(reader.matches("import", true) && reader.hasWhitespace(true)) {

            final String line = reader.readUntil(';').trim();
            
            StringTokenizer tokenizer = new StringTokenizer(line, " ,");
            
            while(tokenizer.hasMoreTokens()) {
            
            	String depName = tokenizer.nextToken();
            	
            	if(context.projInfo.props.isVerbose) {
            		System.out.println("Found dependency "
            				+depName+"' imported from '"+context.source.getInfo().fullName
            				+", importing...");
            	}
            	
            	SourceContext dependency = null;
            	
            	try {
            		
            		/* Is depName the fully qualified name, or does depName exist in the default package ? */
            		dependency = context.parser.parse(context.projInfo, depName, false);
            		
            	} catch (FileNotFoundException e) {
            		
            		try {
            		
	            		/* Is depName in the current package ? */
	            		dependency = context.parser.parse(context.projInfo, context.source.getInfo().getFullName(depName), false);
	            		
            		} catch (FileNotFoundException e2) {
            			
            			throw new Error("While trying to resolve dependency at "+reader.getLocation(), e2);
            			
            		}
            		
				} finally {
					
					if(dependency != null) {
						context.addDependency(dependency);
					}
					
				}
	            
            }
            
            reader.skipWhitespace();
            if(!reader.matches(";", true)) {
                context.err("Expected ';' after import statement.");
            }
            return true;

        }
        
        reader.reset(mark);
        if(reader.matches("include", true) && reader.hasWhitespace(true)) {

        	String names = reader.readUntil(';').trim();
        	StringTokenizer sT = new StringTokenizer(names, ", ");
    		while(sT.hasMoreElements()) {
    			
    			String name = sT.nextToken();
    			IncludeLanguage lang;
    			if(name.startsWith("++")) {
    				name = name.substring(2);
    				lang = IncludeLanguage.CPP;
    			} else {
    				lang = IncludeLanguage.C;
    			}
    			IncludeType type;
    			if(name.startsWith("@")) {
    				name = name.substring(1);
    				type = IncludeType.LOCAL;
    			} else {
    				type = IncludeType.PATHBOUND;    				
    			}
    			IncludePosition pos;
    			if(name.startsWith("%")) {
    				name = name.substring(1);
    				pos = IncludePosition.NOWHERE;
    			} else {
    				pos = IncludePosition.HEADER;    				
    			}
    			context.add(new Include(reader.getLocation(), type, pos, lang, name+".h"));
    		}
        	reader.skipWhitespace();
            if(!reader.matches(";", true)) {
                context.err("Expected ';' after include statement!");
            }
            return true;

        }
        
        reader.reset(mark);
        if (reader.matches("use", true) && reader.hasWhitespace(true)) {
    	
    		String names = reader.readUntil(';').trim();
    		StringTokenizer sT = new StringTokenizer(names, ", ");
    		while(sT.hasMoreElements()) {
    			String name = sT.nextToken();
	    		context.add(new Use(reader.getLocation(), name));
	    		Compiler.libManager.resolveLibraries(name, context.projInfo);
    		}
    		reader.skipWhitespace();
    		if(!reader.matches(";", true)) {
                context.err("Expected semi-colon after use statement!");
            }
    		return true;
    	
        }
        
        return false;
		
	}
	
}
