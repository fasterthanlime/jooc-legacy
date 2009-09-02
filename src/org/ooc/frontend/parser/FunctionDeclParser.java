package org.ooc.frontend.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.GenericType;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.OocDocComment;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class FunctionDeclParser {

	public static FunctionDecl parse(Module module, SourceReader sReader, TokenReader reader, boolean skipFunc) throws IOException {

		int mark = reader.mark();
		
		OocDocComment comment = null;
		if(reader.peek().type == TokenType.OOCDOC) {
			Token token = reader.read();
			comment = new OocDocComment(token.get(sReader), token);
		}
		
		Token startToken= reader.peek();
		
		String name = "";
		Token tName = reader.peek();
		if(tName.isNameToken()) {
			name = tName.get(sReader);
			reader.skip();
			if(reader.read().type != TokenType.COLON) {
				reader.reset(mark);
				return null;
			}
		}
		
		boolean isProto = false;
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isInline = false;
		String externName = null;
		
		Token kw = reader.peek();
		keywordRead: while(true) {
			switch(kw.type) {
			case TokenType.ABSTRACT_KW: reader.skip(); isAbstract = true; break;
			case TokenType.STATIC_KW: reader.skip(); isStatic = true; break;
			case TokenType.FINAL_KW: reader.skip(); isFinal = true; break;
			case TokenType.PROTO_KW: reader.skip(); isProto = true; break;
			case TokenType.INLINE_KW: reader.skip(); isInline = true; break;
			case TokenType.EXTERN_KW: externName = ExternParser.parse(sReader, reader); break;
			default: break keywordRead;
			}
			kw = reader.peek();
		}
		
		if(reader.peek().type == TokenType.FUNC_KW) {
			reader.skip();
		} else if(!skipFunc) {
			reader.reset(mark);
			return null;
		}
		
		String suffix = "";
		List<GenericType> genTypes = null;
		while(true) {
			Token tok = reader.peek();
			if(tok.type == TokenType.TILDE) {
				reader.skip();
				Token tSuffix = reader.peek();
				if(tSuffix.isNameToken()) {
					reader.skip();
					suffix = tSuffix.get(sReader);
				}
			} else if(tok.type == TokenType.LESSTHAN) {
				reader.skip();
				genTypes = new ArrayList<GenericType>();
				TypeParamParser.parse(sReader, reader, genTypes);
			} else break;
		}
		
		FunctionDecl functionDecl = new FunctionDecl(
				name, suffix, isFinal, isStatic, isAbstract, externName, startToken);
		functionDecl.setInline(isInline);
		functionDecl.setProto(isProto);
		if(genTypes != null) {
			for(GenericType genType: genTypes) {
				functionDecl.getGenericTypes().put(genType.getName(), genType);
			}
		}
		if(comment != null) functionDecl.setComment(comment);
		
		ArgumentParser.fill(module, sReader, reader, functionDecl.isExtern(), functionDecl.getArguments());
		
		Token token = reader.peek();
		if(token.type == TokenType.ARROW) {
			reader.skip();
			reader.skipWhitespace();
			Type returnType = TypeParser.parse(module, sReader, reader);
			if(returnType == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected return type after '->'");
			}
			functionDecl.setReturnType(returnType);
		}
		
		if(externName != null || isAbstract) {
			return functionDecl;
		}

		token = reader.readWhiteless();
		if(token.type != TokenType.OPEN_BRACK) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev()),
					"Expected body, e.g. {} after a function name (even for empty functions)");
		}
	
		while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
			reader.skipWhitespace();
		
			if(!LineParser.fill(module, sReader, reader, functionDecl.getBody()) && reader.hasNext()
					&& reader.peek().type != TokenType.CLOS_BRACK) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected statement in function body. Found "+reader.peek()+" instead.");
			}
		}
		reader.skip();
		
		return functionDecl;
	}
	
}
