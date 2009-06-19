package org.ooc.parsers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ooc.backends.ProjectInfo;
import org.ooc.compiler.ContentProvider;
import org.ooc.compiler.SourceInfo;
import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Turns an .ooc source file into a syntax tree.
 * 
 * @author Amos Wenger
 */
public class SourceParser {
	
    private final Parser[] parsers;
    private final Map<String, SourceContext> sources;
	private boolean assembleDefault;
	private final ContentProvider provider;
    
    /**
     * Create a new source parser and initialize all sub-parsers.
     */
    public SourceParser(ContentProvider provider) {
    	
    	// The order matters
    	parsers = new Parser[] {
    			new PreprocessingDirectivesParser(),
        		new DocumentationParser(),
        		new EnumParser(),
        		new BlocksParser(),
        		new PunctuationParser(),
        		new DependenciesParser(),
        		new CTypeParser(),
        		new KeywordsParser(),
        		new ClassDefParser(),
        		new StructDefParser(),
        		new TypeDefParser(),
        		new NinjaFuncDefParser(),
        		new FunctionDefParser(),
        		new OperatorsParser(),
        		new StringLiteralParser(),
        		new CharLiteralParser(),
        		new BooleanLiteralParser(),
        		new NullLiteralParser(),
        		new NumberLiteralParser(),
        		new ControlsParser(),
        		new VariableAccessParser(),
        		new VariableDeclParser(),
        		new FunctionCallParser(),
        		new TypeParser(),
        		new NameParser(),
        		new FunctionReferenceParser(),
        };
    	
    	sources = new HashMap<String, SourceContext> ();
    	assembleDefault = true;
    	this.provider = provider;
    	
    }
    
    /**
     * @return a map of all parsed sources fullnames to their SourceContext.
     */
    public Map<String, SourceContext> getSources() {
		return sources;
	}

    /**
     * Parse a source context from its full name, getting content from said
     * provider, and assemble 
     * @param fullSourceName
     * @return
     * @throws IOException
     */
    public SourceContext parse(final ProjectInfo projInfo, final String fullSourceName) throws IOException {
    	
    	return parse(projInfo, fullSourceName, assembleDefault);
    	
    }
    
    /**
     * Parse a source context from its full name, getting content from said
     * provider, and assemble if asked to.
     * @param assemble
     * @param fullSourceName
     * @return
     * @throws IOException
     */
	public SourceContext parse(final ProjectInfo projInfo, final String fullSourceNameParam, boolean assemble) throws IOException {

		/* The compiler accepts fully qualified source names as well as file paths */
		String fullSourceName = fullSourceNameParam;
		if(fullSourceName.endsWith(".ooc")) {
			fullSourceName = fullSourceName.substring(0, fullSourceName.length() - ".ooc".length());
			fullSourceName = fullSourceName.replace('/', '.');
			while(fullSourceName.startsWith(".")) {
				fullSourceName = fullSourceName.substring(1);
			}
		}
		
		assembleDefault = assemble;
    	SourceContext context;
    	
    	if(sources.containsKey(fullSourceName)) {
    		
    		context = sources.get(fullSourceName);
    		
    	} else {
    	
    		SourceInfo info = new SourceInfo(fullSourceName);
	        final SourceReader reader = provider.getReader(info);
	        context = new SourceContext(projInfo, info, reader, this);
	        sources.put(info.fullName, context);
	
	        if(projInfo.props.isVerbose) {
	        	System.out.println("Compiling source '"+info.fullName+"'");
	        }

	        parse(context);
	        addStandardLib(projInfo,context);
	        
	        if(context.hasErrors()) {
				context.printErrors();
				System.out.println("Has errors, returning before assembling.");
				return context;
			}
	        
	        if(assemble) {
	        	AssemblyManager manager = new AssemblyManager(context);
        		manager.assemble();
        	}
	        
    	}
    	
    	context.setAssembled(true);
    	return context;
        
    }

	private void addStandardLib(ProjectInfo projInfo, SourceContext context) throws IOException {

		if(!context.source.getInfo().fullName.equals("OocLib")) {
			SourceContext oocLib = parse(projInfo, "OocLib");
			context.addDependency(oocLib);
		}
		
	}

	/**
	 * Do one parsing iteration and add the parsed nodes into context
	 * @param context
	 * @return
	 * @throws SyntaxError 
	 */
	private void parse(final SourceContext context) throws IOException {

		try {
		
			while(true) {
			
				if(!context.reader.hasNext()) {
					
					return;
					
				}
			
				if (context.reader.hasWhitespace(true)) {
					
		            // the compiler outputs clean, well-[indented/formatted] code anyway, so ignore original whitespace.
			    	continue;
		            
		        } else if(parsersMatch(context)) {
			    	
		        	continue;
			    	
			    } else {
			    	
					// fatal as long as the compiler is C-incomplete.
					throw new CompilationFailedError(context.reader.getLocation(), "Unexpected input: '"
							+ SourceReader.spelled(context.reader.peek()) + "'");
			
			    }
		    
			}
		
		} catch (SyntaxError e) {
			
			throw new CompilationFailedError(e);
			
		}
	
	}

	/**
	 * Test all parsers, in order.
	 * @throws SyntaxError 
	 */
	private boolean parsersMatch(final SourceContext context) throws IOException, SyntaxError {

		boolean success = false;

		final int mark = context.reader.mark();
		search: for(Parser parser: parsers) {
			if(parser.parse(context)) {
				success = true;
				break search;
			}
			context.reader.reset(mark);
		}
		
		return success;
		
	}

	/**
	 * Clear the source cache
	 */
	public void clearCache() {

		sources.clear();
		
	}

}
