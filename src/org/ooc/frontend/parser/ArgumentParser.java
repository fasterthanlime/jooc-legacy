package org.ooc.frontend.parser;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ArgumentParser {

	public static boolean fill(Module module, SourceReader sReader, TokenReader reader, boolean isExtern, NodeList<Argument> args) throws EOFException, IOException, CompilationFailedError {
		
		if(reader.peek().type == TokenType.OPEN_PAREN) {
			reader.skip();
			boolean comma = false;
			while(true) {
				
				if(reader.peekWhiteless().type == TokenType.CLOS_PAREN) {
					reader.skipWhitespace();
					reader.skip(); // skip the ')'
					break;
				}
				if(comma) {
					if(reader.read().type != TokenType.COMMA) {
						throw new CompilationFailedError(sReader.getLocation(reader.prev()),
								"Expected comma between arguments of a function definition");
					}
				} else {
					if(!parseInto(module, sReader, reader, isExtern, args)) {
						throw new CompilationFailedError(sReader.getLocation(reader.peek()),
								"Expected argument specification of a function definition");
					}
				}
				comma = !comma;
				
			}
			return true;
		}
		return false;
		
	}
	
	public static boolean parseInto(Module module, SourceReader sReader, TokenReader reader, boolean isExtern, NodeList<Argument> args) throws IOException {
		
		int mark = reader.mark();
		
		Token startToken = reader.peek();
		if(startToken.type == TokenType.TRIPLE_DOT) {
			reader.skip();
			VarArg varArg = new VarArg(startToken);
			args.add(varArg);
			return true;
		}
		
		Token token = reader.readWhiteless();
		if(tryRegular(module, sReader, reader, args, mark, token)) return true;
		if(tryAssign(sReader, reader, args, token)) return true;
		if(tryMember(sReader, reader, args, token)) return true;
		reader.reset(mark);
		
		if(isExtern) {
			Type type = TypeParser.parse(module, sReader, reader);
			if(type == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
					"Expected argument type in extern func definition ':'");
			}
			TypeArgument typeArg = new TypeArgument(type, type.startToken);
			args.add(typeArg);
			return true;
		}
				
		reader.reset();
		return false;
		
	}

	protected static boolean tryMember(SourceReader sReader, TokenReader reader,
			NodeList<Argument> args, Token token)
			throws CompilationFailedError, EOFException {
		
		if(token.type != TokenType.DOT) return false;
		
		Token t2 = reader.read();
		if(t2.type != TokenType.NAME) {
			throw new CompilationFailedError(sReader.getLocation(t2),
					"Expecting member variable name in member-assign-argument");
		}
		MemberArgument arg = new MemberArgument(t2.get(sReader), token);
		args.add(arg);
		return true;
	}

	protected static boolean tryAssign(SourceReader sReader, TokenReader reader,
			NodeList<Argument> args, Token token)
			throws CompilationFailedError, EOFException {
		
		if(token.type != TokenType.ASSIGN) return false;
		Token t2 = reader.read();
		if(t2.type != TokenType.NAME) {
			throw new CompilationFailedError(sReader.getLocation(t2),
					"Expecting member variable name in member-assign-argument");
		}
		MemberAssignArgument arg = new MemberAssignArgument(t2.get(sReader), token);
		args.add(arg);
		return true;
	}

	protected static boolean tryRegular(Module module, SourceReader sReader, TokenReader reader,
			NodeList<Argument> args, int mark, Token token)
			throws CompilationFailedError, IOException {
		
		if(!token.isNameToken()) return false;
		
		List<String> names = new ArrayList<String>();
		List<Token> tokens = new ArrayList<Token>();
		names.add(token.get(sReader));
		tokens.add(token);
		while(reader.peek().type == TokenType.COMMA) {
			reader.skip();
			if(reader.peek().type != TokenType.NAME) {
				reader.reset(mark);
				return false;
			}
			Token subToken = reader.read();
			names.add(subToken.get(sReader));
			tokens.add(subToken);
		}

		if(reader.peek().type == TokenType.COLON) {
			reader.skip();
			Type type = TypeParser.parse(module, sReader, reader);
			if(type == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected argument type after its name and ':'");
			}
			for(int i = 0; i < names.size(); i++) {
				RegularArgument regArg = new RegularArgument(type, names.get(i), tokens.get(i));
				args.add(regArg);
			}
			return true;
		}
		reader.reset(mark);
		return false;
		
	}
	
}
