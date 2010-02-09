package org.ooc.frontend.model;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class Tokenizer {

	protected static class Name {
		String name;
		byte tokenType;
		
		public Name(String name, byte tokenType) {
			this.name = name;
			this.tokenType = tokenType;
		}
	}
	
	protected static Name[] names = new Name[] {
		new Name("func", TokenType.FUNC_KW),
		new Name("class", TokenType.CLASS_KW),
		new Name("cover", TokenType.COVER_KW),
		new Name("extern", TokenType.EXTERN_KW),
		new Name("from", TokenType.FROM_KW),
		new Name("if", TokenType.IF_KW),
		new Name("else", TokenType.ELSE_KW),
		new Name("for", TokenType.FOR_KW),
		new Name("while", TokenType.WHILE_KW),
		new Name("true", TokenType.TRUE),
		new Name("false", TokenType.FALSE),
		new Name("null", TokenType.NULL),
		new Name("do", TokenType.DO_KW),
		new Name("match", TokenType.MATCH_KW),
		new Name("case", TokenType.CASE_KW),
		new Name("return", TokenType.RETURN_KW),
		new Name("as", TokenType.AS_KW),
		new Name("const", TokenType.CONST_KW),
		new Name("static", TokenType.STATIC_KW),
		new Name("abstract", TokenType.ABSTRACT_KW),
		new Name("import", TokenType.IMPORT_KW),
		new Name("final", TokenType.FINAL_KW),
		new Name("include", TokenType.INCLUDE_KW),
		new Name("use", TokenType.USE_KW),
		new Name("break", TokenType.BREAK_KW),
		new Name("continue", TokenType.CONTINUE_KW),
		new Name("fallthrough", TokenType.FALLTHR_KW),
		new Name("extends", TokenType.EXTENDS_KW),
		new Name("in", TokenType.IN_KW),
		new Name("version", TokenType.VERSION_KW),
		new Name("proto", TokenType.PROTO_KW),
		new Name("unmangled", TokenType.UNMANGLED_KW),
		new Name("inline", TokenType.INLINE_KW),
		new Name("operator", TokenType.OPERATOR_KW),
		//TODO I'm not sure if those three should be keywords.
		//They are remains from C and can be parsed as NAMEs
		new Name("unsigned", TokenType.UNSIGNED),
		new Name("signed", TokenType.SIGNED),
		new Name("long", TokenType.LONG),
		new Name("union", TokenType.UNION),
		new Name("struct", TokenType.STRUCT),
		new Name("into", TokenType.INTO_KW),
	};
	
	protected static class CharTuple {
		char first;
		byte firstType;
		
		char second;
		byte secondType;
		
		char third;
		byte thirdType;
		
		public CharTuple(char first, byte firstType) {
			this(first, firstType, '\0', (byte) 0);
		}
		
		public CharTuple(char first, byte firstType, char second, byte secondType) {
			this(first, firstType, second, secondType, '\0', (byte) 0);
		}
		
		public CharTuple(char first, byte firstType, char second, byte secondType,
				char third, byte thirdType) {
			this.first = first;
			this.firstType = firstType;
			this.second = second;
			this.secondType = secondType;
			this.third = third;
			this.thirdType = thirdType;
		}

		public Token handle(int index, char c, SourceReader reader) throws EOFException {
			if(c != first) return null;
			reader.read();
			if(second == '\0' || !reader.hasNext() || second != reader.peek()) {
				return new Token(index, 1, firstType);
			}
			reader.read();
			if(third == '\0' || !reader.hasNext() || third != reader.peek()) {
				return new Token(index, 2, secondType);
			}
			reader.read();
			return new Token(index, 3, thirdType);
		}
	}
	
	protected static CharTuple[] chars = new CharTuple[] {
		new CharTuple('(', TokenType.OPEN_PAREN),
		new CharTuple(')', TokenType.CLOS_PAREN),
		new CharTuple('{', TokenType.OPEN_BRACK),
		new CharTuple('}', TokenType.CLOS_BRACK),
		new CharTuple('[', TokenType.OPEN_SQUAR),
		new CharTuple(']', TokenType.CLOS_SQUAR),
		//new CharTuple('=', TokenType.ASSIGN, '=', TokenType.EQUALS),
		new CharTuple('.', TokenType.DOT, '.', TokenType.DOUBLE_DOT, '.', TokenType.TRIPLE_DOT),
		new CharTuple(',', TokenType.COMMA),
		new CharTuple('%', TokenType.PERCENT),
		new CharTuple('~', TokenType.TILDE),
		new CharTuple(':', TokenType.COLON, '=', TokenType.DECL_ASSIGN),
		new CharTuple('!', TokenType.BANG, '=', TokenType.NOT_EQUALS),
		//new CharTuple('&', TokenType.AMPERSAND, '&', TokenType.DOUBLE_AMPERSAND),
		new CharTuple('|', TokenType.PIPE, '|', TokenType.DOUBLE_PIPE),
		new CharTuple('?', TokenType.QUEST),
		new CharTuple('#', TokenType.HASH),
		new CharTuple('@', TokenType.AT),
		new CharTuple('+', TokenType.PLUS, '=', TokenType.PLUS_ASSIGN),
		new CharTuple('*', TokenType.STAR, '=', TokenType.STAR_ASSIGN),
		new CharTuple('^', TokenType.CARET),
		new CharTuple('<', TokenType.LESSTHAN),
		new CharTuple('>', TokenType.GREATERTHAN),
	};
	
	public List<Token> parse(SourceReader reader) throws IOException {
		
		List<Token> tokens = new ArrayList<Token>();
		
		reading: while(reader.hasNext()) {
			
			reader.skipChars("\t ");
			if(!reader.hasNext()) {
				break;
			}
			
			int index = reader.mark();
			
			char c = reader.peek();
			if(c == ';' || c == '\n' || c == '\r') {
				reader.read();
				while(reader.hasNext() && (reader.peek() == '\n' || reader.peek() == '\r')) {
					reader.read();
				}
				tokens.add(new Token(index, 1, TokenType.LINESEP));
				continue;
			}
			
			if(c == '\\') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '\\') {
					reader.read();
					tokens.add(new Token(index, 2, TokenType.DOUBLE_BACKSLASH));
				} else if(c2 == '\n') {
					reader.read(); // Just skip both of'em (line continuation)
				} else {
					tokens.add(new Token(index, 1, TokenType.BACKSLASH));
				}
				continue;
			}
			
			for(CharTuple candidate: chars) {
				Token token = candidate.handle(index, c, reader);
				if(token != null) {
					tokens.add(token);
					continue reading;
				}
			}

			
			if(c == '"') {
				reader.read();
				// TODO: optimize. readStringLiteral actually stores it into a String, but we don't care
				try {
					reader.readStringLiteral();
				} catch(EOFException eof) {
					throw new CompilationFailedError(reader.getLocation(index, 0), "Never-ending string literal (reached end of file)");
				}
				tokens.add(new Token(index + 1,
						reader.mark() - index - 2,
						TokenType.STRING_LIT));
				continue;
			}
			
			if(c == '\'') {
				reader.read();
				try {
					reader.readCharLiteral();
					tokens.add(new Token(index + 1, 
							reader.mark() - index - 2,
							TokenType.CHAR_LIT));
					continue;
				} catch(SyntaxError e) {
					throw new CompilationFailedError(reader.getLocation(index, 0), e.getMessage());
				}
			}
			
			if(c == '/') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '=') {
					reader.read();
					tokens.add(new Token(index, 2, TokenType.SLASH_ASSIGN));
				} else if(c2 == '/') {
					reader.readLine();
					tokens.add(new Token(index, 1, TokenType.LINESEP));
				} else if(c2 == '*') {
					reader.read();
					char c3 = reader.peek();
					if(c3 == '*') {
						reader.read();
						reader.readUntil(new String[] {"*/"}, true);
						tokens.add(new Token(index, reader.mark() - index, TokenType.OOCDOC));
					} else {
						reader.readUntil(new String[] {"*/"}, true);
					}
				} else {
					tokens.add(new Token(index, 1, TokenType.SLASH));
				}
				continue;
			}
			
			if(c == '-') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '>') {
					reader.read();
					tokens.add(new Token(index, 2, TokenType.ARROW));
				} else if(c2 == '=') {
					reader.read();
					tokens.add(new Token(index, 2, TokenType.MINUS_ASSIGN));
				} else {
					tokens.add(new Token(index, 1, TokenType.MINUS));
				}
				continue;
			}
		
			if(c == '=') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == '>') {
					reader.read();
					tokens.add(new Token(index, 2, TokenType.DOUBLE_ARROW));
				} else if(c2 == '=') {
					reader.read();
					tokens.add(new Token(index, 2, TokenType.EQUALS));
				} else {
					tokens.add(new Token(index, 1, TokenType.ASSIGN));
				}
				continue;
			}
			
			if(c == '&') {
				// read the precious one
				reader.rewind(1);
				char cprev = reader.read();
				reader.read(); // skip the '&'
				boolean binary = false;
				if(cprev == ' ') binary = true;
				char c2 = reader.peek();
				if(c2 == '&') {
					reader.read();
					tokens.add(new Token(index, 2, TokenType.DOUBLE_AMPERSAND));
				} else if(binary) {
					tokens.add(new Token(index, 1, TokenType.BINARY_AND));
				} else {
					tokens.add(new Token(index, 1, TokenType.AMPERSAND));
				}
				continue;
			}
			
			if(c == '0') {
				reader.read();
				char c2 = reader.peek();
				if(c2 == 'x') {
					reader.read();
					String lit = reader.readMany("0123456789abcdefABCDEF", "_", true);
					if(lit.length() == 0) {
						throw new CompilationFailedError(reader.getLocation(index, 0), "Empty hexadecimal number literal");
					}
					tokens.add(new Token(index + 2, reader.mark()
							- index - 2, TokenType.HEX_INT));
					continue;
				} else if(c2 == 'c') {
					reader.read();
					String lit = reader.readMany("01234567", "_", true);
					if(lit.length() == 0) {
						throw new CompilationFailedError(reader.getLocation(index, 0), "Empty octal number literal");
					}
					tokens.add(new Token(index + 2, reader.mark()
							- index - 2, TokenType.OCT_INT));
					continue;
				} else if(c2 == 'b') {
					reader.read();
					String lit = reader.readMany("01", "_", true);
					if(lit.length() == 0) {
						throw new CompilationFailedError(reader.getLocation(index, 0), "Empty binary number literal");
					}
					tokens.add(new Token(index + 2, reader.mark()
							- index - 2, TokenType.BIN_INT));
					continue;
				}
			}
			
			if(Character.isDigit(c)) {
				reader.readMany("0123456789", "_", true);
				if(reader.peek() == '.') {
					reader.read();
					if(reader.peek() != '.') {
						reader.readMany("0123456789", "_", true);
						reader.readExponent();
						tokens.add(new Token(index, reader.mark() - index,
								TokenType.DEC_FLOAT));
						continue;
					}
					reader.rewind(1);
				}
				
				byte type = TokenType.DEC_INT;
				
				if (reader.readExponent()) {
					type = TokenType.DEC_FLOAT;
				}
				
				tokens.add(new Token(index, reader.mark() - index, type));
				continue;
			}
			
			if(reader.skipName()) {
				String name = reader.getSlice(index, reader.mark() - index);
				for(Name candidate: names) {
					if(candidate.name.equals(name)) {
						tokens.add(new Token(index, name.length(), candidate.tokenType));
						continue reading;
					}
				}
				tokens.add(new Token(index, name.length(), TokenType.NAME));
				continue reading;
			}
			throw new CompilationFailedError(reader.getLocation(index, 1),
					"Unexpected input '"+SourceReader.spelled(reader.peek())+"'");
			
		}
		
		tokens.add(new Token(reader.mark(), 0, TokenType.LINESEP));

		return tokens;
	}
	
}
