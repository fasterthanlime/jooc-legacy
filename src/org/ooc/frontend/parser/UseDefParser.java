package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.UseDef;
import org.ooc.middle.UseDef.Requirement;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class UseDefParser {

	protected static Map<String, UseDef> cache = new HashMap<String, UseDef>();
	
	public static UseDef parse(String identifier, SourceReader sReader, Token token, BuildParams params) throws IOException {
		
		UseDef cached = cache.get(identifier);
		if(cached != null) return cached;
		
		File file = findUse(identifier+".use", params);
		if(file == null) {
			throw new CompilationFailedError(sReader.getLocation(token),
					"Use not found in the ooc library path: "+identifier
					+"\n\nTo install ooc libraries, copy their directories to /usr/lib/ooc/" +
					"\nIf you want to install libraries elsewhere, use the OOC_LIBS environment variable," +
					"\nwhich is the path ooc will scan for .use files (in this case, "+identifier+".use)" +
					"\nFor more informations, see http://docs.ooc-lang.org/libs.html" +
					"\n-------------------");
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
			} else if(id.equals("LibPaths")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) {
					String libPath = st.nextToken().trim();
					File libFile = new File(file.getParent(), libPath);
					if(!libFile.isAbsolute()) {
						libPath = libFile.getCanonicalPath();
					}
					def.getLibPaths().add(libPath);
				} 
			} else if(id.equals("IncludePaths")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) {
					String includePath = st.nextToken().trim();
					if(!(new File(includePath).isAbsolute())) {
						includePath = new File(file.getParent(), includePath).getCanonicalPath();
					}
					def.getIncludePaths().add(includePath);
				} 
			} else if(id.equals("Requires")) {
				StringTokenizer st = new StringTokenizer(value, ",");
				while(st.hasMoreTokens()) {
					def.getRequirements().add(new Requirement(st.nextToken().trim(), new int[] {0}));
				}
			} else if(id.equals("SourcePath")) {
				File path = new File(value);
				if(!path.isAbsolute()) {
					path = new File(file.getParent(), value);
				}
				if(params.verbose) {
					System.out.println("Adding "+path+" to sourcePath from "+def);
				}
				params.sourcePath.add(path.getPath());
				
				
			}
			
			reader.skipWhitespace();
		}
		
		for(Requirement req: def.getRequirements()) {
			req.setDef(parse(req.getName(), sReader, token, params));
		}
		
		return def;
		
	}

	private static File findUse(String fileName, BuildParams params) {
		
		Set<File> set = new HashSet<File>();
		set.add(params.libsPath);
		
		int i = 0;
		while(!set.isEmpty()) {
			Set<File> nextSet = new HashSet<File>();
			for(File candidate: set) {
				if(candidate.getPath().endsWith(fileName)) {
					return candidate;
				} else if(candidate.isDirectory()) {
					for(File child: candidate.listFiles()) {
						nextSet.add(child);
					}
				}
			}
			set = nextSet;
		}
		
		return null;
		
	}

	
	
}
