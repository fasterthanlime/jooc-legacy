package org.ooc.frontend.parser;

import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ClassDeclParser {

	public static ClassDecl parse(Module module, SourceReader sReader, TokenReader reader) {
		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token token = reader.read();
			comment = new OocDocComment(token.get(sReader), token);
			// allow an arbitrary number of newlines after the oocdoc comment.
			while(reader.peek().type == TokenType.LINESEP)
				reader.skip();
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
		List<TypeParam> genTypes = null;
		
		if(reader.readWhiteless().type == TokenType.CLASS_KW) {
		
			if(reader.peek().type == TokenType.LESSTHAN) {
				reader.skip();
				genTypes = new ArrayList<TypeParam>();
				TypeParamParser.parse(sReader, reader, genTypes);
			}
			
			Type superType = null;
			if(reader.peek().type == TokenType.EXTENDS_KW) {
				reader.skip();
				superType = TypeParser.parse(module, sReader, reader);
			}
			
			reader.skipWhitespace();
			Token t2 = reader.read();
			if(t2.type != TokenType.OPEN_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(t2),
						"Expected opening bracket to begin class declaration.");
			}
			
			if(superType != null && name.equals(superType.getName())) {
				throw new CompilationFailedError(sReader.getLocation(tName), "A class cannot extends itself!");
			}
			
			ClassDecl classDecl = new ClassDecl(name, superType, isAbstract, module, tName);
			if(genTypes != null) for(TypeParam genType: genTypes) {
				classDecl.addTypeParam(genType);
			}
			classDecl.addInit();
			module.parseStack.push(classDecl);
			if(comment != null) classDecl.setComment(comment);
			
			while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
			
				if(reader.skipWhitespace()) continue;
				
				VariableDecl varDecl = VariableDeclParser.parse(module, sReader, reader);
				if(varDecl != null) {
					Token tok = reader.read();
					if(tok.type != TokenType.LINESEP && tok.type != TokenType.OOCDOC) {
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
						"Expected variable declaration or function declaration in a class declaration, got "+reader.peek());
			
			}
			reader.skip();
			
			module.parseStack.pop(classDecl);
			return classDecl;
			
		}
		
		reader.reset(mark);
		return null;
	}
	
}
