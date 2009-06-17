package org.ooc.compiler;

import java.util.Collection;

import org.ooc.backends.ProjectInfo;
import org.ooc.compiler.libraries.LibraryManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.MaxedOutPassLimit;
import org.ooc.errors.SourceContext;
import org.ooc.parsers.SourceParser;

/**
 * ooc's public interface to compile
 * 
 * @author Amos Wenger
 */
public class Compiler {
	
	private final SourceParser parser;

	private FileContentProvider provider;
	
	/**
	 * @see LibraryManager
	 */
	public final static LibraryManager libManager = new LibraryManager();
	
	/**
	 * Default constructor.
	 */
	public Compiler() {
		
		this.provider = new FileContentProvider(null);
		this.parser = new SourceParser(provider);
		
	}
	
	/**
	 * Compile sourceName, with all specified build properties, and return 0 on
	 * success, and a non-zero value on failure. The specified projInfo will be
	 * filled with information (e.g. executables, modules) about the compilation.
	 * @param props the build properties
	 * @param sourceName the full name of the source to build e.g. "my.package.MyClass"
	 * @param projInfo a project info that will contain everything there is to know
	 * about the compilation (e.g. executables/modules, dependencies, etc.)
	 * @return 0 on success, any other value if there's any problem
	 * @throws Exception
	 */
	public synchronized int compile(ProjectInfo projInfo, String sourceName) throws Exception {
		
		BuildProperties props = projInfo.props;
		provider.setSourcePath(props.sourcePath);
		
		try {
			
			SourceContext mainSource = parser.parse(projInfo, sourceName, true);
			if(!mainSource.isAssembled()) {
				System.out.println(mainSource.source.getInfo().fullName+" failed to compile, abandoning.");
				return 1;
			}
			
			projInfo.addSourceRecursive(mainSource);
	        
	        if(props.backend != null) {
	        	
	        	libManager.resolveLibraries("gc", projInfo);
	        	return props.backend.compile(projInfo, props);
	        	
	        }
	        
	        return 0;
	        
		} catch(MaxedOutPassLimit e) {
			
			return 1;
			
		} catch(CompilationFailedError e) {
			
			System.err.println(e.getMessage());
			return 1;
			
		}

    }
	
	/**
	 * Clear the source cache
	 */
	public void clearCache() {
		
		parser.clearCache();
		
	}
	
	/**
	 * @return all sources processed by this compiler
	 */
	public Collection<SourceContext> getSources() {
		
		return parser.getSources().values();
		
	}

}
