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
						module.getTypes().put(classDecl.getName(), classDecl);
						continue;
					}
				}
				
				{
					CoverDecl coverDecl = CoverDeclParser.parse(module, sReader, reader);
					if(coverDecl != null) {
						module.getTypes().put(coverDecl.getName(), coverDecl);
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
				if(UseParser.fill(sReader, reader, module.getUses(), parser.params)) continue;
				if(CommentParser.parse(sReader, reader) != null) continue;
				
				Token errToken = reader.peek();
				throw new CompilationFailedError(sReader.getLocation(errToken),
						"Expected declaration, include, or import in source unit, but got "+errToken);
				
			}
			
			for(Import imp: module.getImports()) {
				String path = imp.getPath() + ".ooc";
				if(path.startsWith("..")) {
					path = FileUtils.resolveRedundancies(new File(module.getParentPath(), path)).getPath();
				}
				
				File impFile = parser.params.sourcePath.getElement(path);
				if(impFile == null) {
					path = module.getParentPath() + "/" + path;
					impFile = parser.params.sourcePath.getElement(path);
					if(impFile == null) {
						throw new OocCompilationError(imp, module, "Module not found in sourcepath: "+imp.getPath());
					}
				}
				
				Module cached = cache.get(path);
				
				if(cached == null || new File(impFile, path).lastModified() > cached.lastModified) {
					if(cached != null) {
						System.out.println(path+" has been changed, recompiling...");
					}
					cached = parser.parse(path, impFile, imp);
				}
				imp.setModule(cached);
			}
		
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	static void addLangImports(Module module, Parser parser) {

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
