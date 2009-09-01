package org.ooc.frontend.parser;

import java.io.IOException;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ubi.SourceReader;

public class DeclarationParser {

	public static Declaration parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();
		
		VariableDecl varDecl = VariableDeclParser.parse(module, sReader, reader);
		if(varDecl != null) return varDecl;
		
		OpDecl opDecl = OpDeclParser.parse(module, sReader, reader);
		if(opDecl != null) return opDecl;
		
		FunctionDecl funcDecl = FunctionDeclParser.parse(module, sReader, reader, false);
		if(funcDecl != null) return funcDecl;
		
		ClassDecl classDecl = ClassDeclParser.parse(module, sReader, reader);
		if(classDecl != null) return classDecl;
		
		CoverDecl coverDecl = CoverDeclParser.parse(module, sReader, reader);
		if(coverDecl != null) return coverDecl;
		
		reader.reset(mark);
		return null;
	}
	
}
