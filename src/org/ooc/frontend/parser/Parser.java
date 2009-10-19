package org.ooc.frontend.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Tokenizer;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class Parser {
	
	protected BuildParams params;
	protected Module mainModule;
	
	public Parser(BuildParams params) {
		this.params = params;
	}
	
	public Module parse(final String path) throws IOException {
		final File file = params.sourcePath.getElement(path);
		if(file == null) {
			throw new CompilationFailedError(null, "File "+path+" not found in sourcePath."
				+" sourcePath = "+params.sourcePath);
		}
		return parse(path, file, null);
	}

	public Module parse(final String path, final File element, Import imp) throws IOException {
		if(params.verbose)
			System.out.println("Parsing "+path);
		
		File file = new File(element, path);
		
		final SourceReader sReader = SourceReader.getReaderFromFile(file);
		final List<Token> tokens = new Tokenizer().parse(sReader);
		
		final String fullName = path.substring(0, path.lastIndexOf('.'))
			.replace(File.separatorChar, '.').replace('/', '.');
		
		final Module module = new Module(fullName, element, sReader);
		ModuleParser.cache.put(path, module);
		if(imp != null) imp.setModule(module);
		
		ModuleParser.parse(module, fullName, file,
				sReader, new TokenReader(tokens), Parser.this);
		//new XStream().toXML(module, new FileWriter(file.getName()+".xml"));
		return module;
	}
		
}
