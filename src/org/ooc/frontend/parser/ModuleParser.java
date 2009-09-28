package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ooc.middle.OocCompilationError;
import org.ooc.utils.FileUtils;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ModuleParser {

	// path -> module
	public final static Map<String, Module> cache = new HashMap<String, Module>();
	
	public static void parse(final Module module, final String fullName, final File file, final SourceReader sReader,
			final TokenReader reader, final Parser parser) {
		
		module.lastModified = file.lastModified();
		cache.put(module.getFullName(), module);
		
		try {
			addLangImports(module, parser);
			
			while(reader.hasNext()) {
	
				if(reader.peek().type == TokenType.LINESEP) {
					reader.skip();
					continue;
				}
				
				{
					ClassDecl classDecl = ClassDeclParser.parse(module, sReader, reader);
					if(classDecl != null) {
						module.getTypes().add(classDecl.getName(), classDecl);
						continue;
					}
				}
				
				{
					CoverDecl coverDecl = CoverDeclParser.parse(module, sReader, reader);
					if(coverDecl != null) {
						module.getTypes().add(coverDecl.getName(), coverDecl);
						continue;
					}
				}
				
				{
					OpDecl opDecl = OpDeclParser.parse(module, sReader, reader);
					if(opDecl != null) {
						module.getOps().add(opDecl);
						continue;
					}
				}
				
				Declaration declaration = DeclarationParser.parse(module, sReader, reader);
				if(declaration != null) {
					if(declaration instanceof VariableDecl) {
						module.getBody().add(new Line(declaration));
					} else {
						module.getBody().add(declaration);
					}
					continue;
				}
				
				if(LineParser.fill(module, sReader, reader, module.getLoadFunc().getBody())) continue;
				if(IncludeParser.fill(sReader, reader, module.getIncludes())) continue;
				if(ImportParser.fill(sReader, reader, module.getImports())) continue;
				if(UseParser.fill(sReader, reader, module.getUses())) continue;
				if(CommentParser.parse(sReader, reader) != null) continue;
				
				Token errToken = reader.peek();
				throw new CompilationFailedError(sReader.getLocation(errToken),
						"Expected declaration, include, or import in source unit, but got "+errToken);
				
			}
			
			for(Import imp: module.getImports()) {
				Module cached = cache.get(imp.getName());
				String path = imp.getPath() + ".ooc";
				if(path.startsWith("..")) {
					path = FileUtils.resolveRedundancies(new File(module.getParentPath(), path)).getPath();
				}
				
				File impFile = parser.params.sourcePath.getFile(path);
				if(impFile == null) {
					path = module.getParentPath() + "/" + path;
					impFile = parser.params.sourcePath.getFile(path);
					if(impFile == null) {
						throw new OocCompilationError(imp, module, "Module not found in sourcepath: "+imp.getPath());
					}
				}
				if(cached == null || impFile.lastModified() > cached.lastModified) {
					if(cached != null) {
						System.out.println(path+" has been changed, recompiling...");
					}
					cached = parser.parse(path, impFile, imp);
					cache.put(imp.getPath(), cached);
				}
				imp.setModule(cached);
			}
		
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	static void addLangImports(Module module, Parser parser) {
		
		if(module.getFullName().startsWith("lang.")) {
			if(!module.getFullName().equals("lang.Object")) {
				module.getImports().add(new Import("lang/Object", Token.defaultToken));
			}
			if(!module.getFullName().equals("lang.ooclib")) {
				module.getImports().add(new Import("lang/ooclib", Token.defaultToken));
			}
			return;
		}
		
		Collection<String> paths = parser.params.sourcePath.getRelativePaths("lang");
		for(String path: paths) {
			if(path.toLowerCase().endsWith(".ooc")) {
				path = path.substring(0, path.length() - 4);
				if(!path.equals(module.getPath())) {
					module.getImports().add(new Import(path, Token.defaultToken));
				}
			}
		}
		
	}

	public static void clearCache() {
		cache.clear();
	}
	
}
