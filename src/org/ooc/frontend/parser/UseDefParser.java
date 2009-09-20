package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.ooc.middle.UseDef;
import org.ooc.middle.UseDef.Requirement;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class UseDefParser {

	protected static Map<String, UseDef> cache = new HashMap<String, UseDef>();
	
	public static UseDef parse(String identifier, BuildParams params) throws IOException {
		
		UseDef cached = cache.get(identifier);
		if(cached != null) return cached;
		
		File file = params.sourcePath.getFile(identifier+".use");
		if(file == null) {
			throw new CompilationFailedError(null, "Use not found in the sourcepath: "+identifier);
		}
		
		UseDef def = new UseDef(identifier);
		cache.put(identifier, def);
		
		SourceReader reader = SourceReader.getReaderFromFile(file);
		while(reader.hasNext()) {
			reader.skipWhitespace();
			
			if(reader.matches("#", false)) {
				reader.skipLine();
				continue;
			}
			
			if(reader.matches("=", false)) {
				reader.skipLine();
				continue;
			}
			
			String id = reader.readUntil(':', false).trim();
			reader.read(); // skip the ':'
			String value = reader.readLine().trim();
			if(id.equals("Name")) {
				def.setName(value);
			} else if(id.equals("Description")) {
				def.setDescription(value);
			} else if(id.equals("Pkgs")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) def.getPkgs().add(st.nextToken().trim());
			} else if(id.equals("Libs")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) def.getLibs().add(st.nextToken().trim()); 
			} else if(id.equals("Includes")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) def.getIncludes().add(st.nextToken().trim()); 
			} else if(id.equals("Lib-Paths")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) {
					String libPath = st.nextToken().trim();
					File libFile = new File(file.getParent(), libPath);
					if(!libFile.isAbsolute()) {
						libPath = libFile.getCanonicalPath();
					}
					def.getLibPaths().add(libPath);
				} 
			} else if(id.equals("Include-Paths")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) {
					String includePath = st.nextToken().trim();
					File includeFile = new File(file.getParent(), includePath);
					if(!includeFile.isAbsolute()) {
						includePath = includeFile.getCanonicalPath();
					}
					def.getIncludePaths().add(includePath);
				} 
			} else if(id.equals("Requires")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) {
					def.getRequirements().add(new Requirement(st.nextToken().trim(), new int[] {0}));
				}
			}
			
			reader.skipWhitespace();
		}
		
		for(Requirement req: def.getRequirements()) {
			req.setDef(parse(req.getName(), params));
		}
		
		return def;
		
	}

	
	
}
