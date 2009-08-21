package org.ooc.parsers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ooc.compiler.BuildProperties;
import org.ooc.compiler.pkgconfig.PkgInfo;
import org.ooc.nodes.CHeader;
import org.ooc.nodes.types.Type;
import org.ooc.nodes.types.TypeDef;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * A simple, dumb, C header parser.
 * Used mostly to retrieve types. 
 * 
 * @author Amos Wenger
 */
public class CHeaderParser {

	/** Map paths => CHeader structures */
	protected static final Map<String, CHeader> cache = new HashMap<String, CHeader>();
	
	/**
	 * Parse a C header from specified path.
	 * @param path The path of the C header, as specified between <> in the
	 * C preprocessor include directive, e.g. #include <header.h>
	 * @param props 
	 * @return a structure holding the parsed data
	 * @throws IOException
	 */
	public static CHeader parse(String path, BuildProperties props) throws IOException {
		
		SourceReader reader;
		
		CHeader cached = cache.get(path);
		if(cached != null) {
			return cached;
		}
		
		reader = findAndOpenHeader(path, props);
		if(reader == null) {
			return null;
		}
		//System.out.println("Parsing "+reader.getLocation());
		
		CHeader header = new CHeader(path);
		cache.put(path, header);
		
		boolean finished = false;
		while(!finished) {
			finished = parse(reader, header, props);
		}
		
		//System.out.println("Finished reading <"+path+">, has "+header.numTypeDefs()+" typeDefs!");
		
		return header;
		
	}

	protected static SourceReader findAndOpenHeader(String path, BuildProperties props) throws IOException {

		File file = new File("/usr/include/"+path);
		if(file.exists()) {
			return SourceReader.getReaderFromFile(file);
		}
		
		file = new File("/usr/local/include/"+path);
		if(file.exists()) {
			return SourceReader.getReaderFromFile(file);
		}
		
		for(String includePath: props.incPath) {
			file = new File(includePath, path);
			if(file.exists()) {
				return SourceReader.getReaderFromFile(file);
			}
		}
		
		for(PkgInfo pkg: props.pkgInfos) {
			for(String includePath: pkg.includePaths) {
				file = new File(includePath, path);
				if(file.exists()) {
					return SourceReader.getReaderFromFile(file);
				}
			}
		}
		
		//throw new CompilationFailedError("Can't find header file <"+path+">");
		
		if(!path.equals("stddef.h") && !path.equals("stdbool.h")
				&& !path.equals("stdarg.h") && !path.equals("float.h") && !path.equals("windows.h")) {
			System.err.println("Can't find header file <"+path+">");
		}
		
		return null;
		
	}

	protected static boolean parse(SourceReader reader, CHeader header, BuildProperties props) throws IOException {

		if(!reader.hasNext()) {
			return true;
		}
		
		if (reader.hasWhitespace(true)) {
	    	return false;
        }
	    
		try {
			TypeDef typeDef = TypeDefParser.read(reader);
	    	if(typeDef != null) {
	    		typeDef.getType().isResolved = true;
	    		header.addTypeDef(typeDef.getName(), typeDef);
	    		return false;
	    	}
		} catch(SyntaxError e) {
			// FIXME wrong design: we have to ignore exceptions to get correct behavior
			// this is horrible performance-wise and design-wise. We should get this fixed.
		}
		
		int mark = reader.mark();
		if(reader.matches("struct", true) && reader.hasWhitespace(true)) {
			String typeName = "struct " + reader.readName();
			header.addTypeDef(typeName, new TypeDef(reader.getLocation(), 
					Type.baseType(typeName), typeName));
		} else {
			reader.reset(mark);
		}
		
		if(reader.backMatches('\n', true) && reader.matches("#include", true) && reader.hasWhitespace(true)) {
			if(reader.matches("<", true)) {
				String path = reader.readUntil('>');
				CHeader dep = parse(path, props);
				if(dep != null) {
					header.addDependency(dep);
				}
			}
		}
		
		// Skip everything we don't understand.
		reader.read();
		return false;
		
	}

}
