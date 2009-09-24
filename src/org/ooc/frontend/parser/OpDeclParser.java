package org.ooc.frontend.parser;

import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.OpDecl.OpType;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class OpDeclParser {

	public static OpDecl parse(Module module, SourceReader sReader, TokenReader reader) {
		
		Token startToken = reader.peek();
		if(startToken.type != TokenType.OPERATOR_KW) return null;
		reader.skip();
		
		OpType type;
		
		Token token = reader.read();
		if(token.type == TokenType.PLUS) {
			type = OpType.ADD;
		} else if(token.type == TokenType.MINUS) {
			type = OpType.SUB;
		} else if(token.type == TokenType.STAR) {
			type = OpType.MUL;
		} else if(token.type == TokenType.SLASH) {
			type = OpType.DIV;
		} else if(token.type == TokenType.PLUS_ASSIGN) {
			type = OpType.ADD_ASS;
		} else if(token.type == TokenType.MINUS_ASSIGN) {
			type = OpType.SUB_ASS;
		} else if(token.type == TokenType.STAR_ASSIGN) {
			type = OpType.MUL_ASS;
		} else if(token.type == TokenType.SLASH_ASSIGN) {
			type = OpType.DIV_ASS;
		} else if(token.type == TokenType.ASSIGN) {
			type = OpType.ASS;
		} else if(token.type == TokenType.EQUALS) {
			type = OpType.EQ;
		} else if(token.type == TokenType.NOT_EQUALS) {
			type = OpType.NE;
		} else if(token.type == TokenType.GREATERTHAN) {
			type = OpType.GT;
		} else if(token.type == TokenType.GREATERTHAN_EQUALS) {
			type = OpType.GTE;
		} else if(token.type == TokenType.LESSTHAN) {
			type = OpType.LT;
		} else if(token.type == TokenType.LESSTHAN_EQUALS) {
			type = OpType.LTE;
		} else if(token.type == TokenType.OPEN_SQUAR) {
			if(reader.peek().type == TokenType.CLOS_SQUAR) {
				reader.skip();
				if(reader.peek().type == TokenType.ASSIGN) {
					reader.skip();
					type = OpType.IDX_ASS;
				} else {
					type = OpType.IDX;
				}
			} else {
				throw new CompilationFailedError(null, "Unexpected token "+reader.peek()
						+". You're probably trying to override [] (indexing) or []= (indexed assign)");
			}
		} else {
			throw new CompilationFailedError(null, "Trying to overload unknown operator "+token);
		}
		
		FunctionDecl decl = FunctionDeclParser.parse(module, sReader, reader, true);
		if(decl == null) {
			throw new CompilationFailedError(null, "Expected function after operator overloading of "+type);
		}
		
		return new OpDecl(type, decl, startToken);
		
	}

}
