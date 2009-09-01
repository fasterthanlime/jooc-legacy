package org.ooc.frontend.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ClassDeclParser {

	public static ClassDecl parse(Module module, SourceReader sReader, TokenReader reader) throws IOException {
		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token token = reader.read();
			comment = new OocDocComment(token.get(sReader), token);
		}
		
		String name = "";
		Token tName = reader.peek();
		if(tName.type != TokenType.NAME) {
			reader.reset(mark);
			return null;
		}
			
		name = tName.get(sReader);
		reader.skip();
		if(reader.read().type != TokenType.COLON) {
			reader.reset(mark);
			return null;
		}
		
		boolean isAbstract = reader.peek().type == TokenType.ABSTRACT_KW;
		if(isAbstract) reader.skip();
		List<GenericType> genTypes = null;
		
		if(reader.readWhiteless().type == TokenType.CLASS_KW) {
		
			if(reader.peek().type == TokenType.LESSTHAN) {
				reader.skip();
				genTypes = new ArrayList<GenericType>();
				TypeParamParser.parse(sReader, reader, genTypes);
			}
			
			String superName = "";
			if(reader.peek().type == TokenType.EXTENDS_KW) {
				reader.skip();
				Token tSuper = reader.read();
				if(tSuper.type != TokenType.NAME) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Expected super-class name after the from keyword.");
				}
				superName = tSuper.get(sReader);
			}
			
			Token t2 = reader.read();
			if(t2.type != TokenType.OPEN_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(t2),
						"Expected opening bracket to begin class declaration.");
			}
			
			ClassDecl classDecl = new ClassDecl(name, superName, isAbstract, tName);
			if(comment != null) classDecl.setComment(comment);
			
			while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
			
				if(reader.skipWhitespace()) continue;
				
				VariableDecl varDecl = VariableDeclParser.parse(module, sReader, reader);
				if(varDecl != null) {
					if(reader.read().type != TokenType.LINESEP) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected semi-colon after variable declaration in class declaration");
					}
					classDecl.addVariable(varDecl);
					continue;
				}
				
				FunctionDecl funcDecl = FunctionDeclParser.parse(module, sReader, reader, false);
				if(funcDecl != null) {
					classDecl.addFunction(funcDecl);
					continue;
				}
				
				if(reader.peek().type == TokenType.OOCDOC) {
					reader.read();
					continue;
				}
				
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected variable declaration or function declaration in a class declaration, got "+reader.peek().type);
			
			}
			reader.skip();
			if(genTypes != null) {
				for(GenericType genType: genTypes) {
					classDecl.getGenericTypes().put(genType.getName(), genType);
				}
			}
			return classDecl;
			
		}
		
		reader.reset(mark);
		return null;
	}
	
}
