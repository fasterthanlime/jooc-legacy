package org.ooc.frontend.parser;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BinaryCombination;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.Match;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Mod;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Ternary;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.Assignment.Mode;
import org.ooc.frontend.model.BinaryCombination.BinaryComp;
import org.ooc.frontend.model.Compare.CompareType;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class ExpressionParser {

	public static Expression parse(Module module, SourceReader sReader, TokenReader reader) {
		return parse(module, sReader, reader, false);
	}
	
	public static Expression parse(Module module, SourceReader sReader, TokenReader reader, boolean noDecl) {
		
		int mark = reader.mark();
		
		Token firstToken = reader.peek();
		if(firstToken.type == TokenType.BANG) {
			reader.skip();
			Expression inner = ExpressionParser.parse(module, sReader, reader, noDecl);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			return new Not(inner, firstToken);
		}
		
		if(firstToken.type == TokenType.MINUS) {
			reader.skip();
			Expression inner = ExpressionParser.parse(module, sReader, reader, noDecl);
			if(inner == null) {
				reader.reset(mark);
				return null;
			}
			return new Sub(new IntLiteral(0, Format.DEC, firstToken), inner, firstToken);
		}
		
		Expression expr = null;
		if(reader.peek().type == TokenType.OPEN_PAREN) {
			reader.skip();
			expr = parse(module, sReader, reader, noDecl);
			expr = new Parenthesis(expr, expr.startToken);
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.prev())
						, "Expected closing parenthesis.");
			}
		} else {
			expr = parseFlatNoparen(module, sReader, reader, noDecl);
		}
		
		if(expr == null) return null;
		
		while(reader.hasNext()) {
			
			Token token = reader.peek();
			
			if(token.isNameToken()) {
				FunctionCall call = FunctionCallParser.parse(module, sReader, reader);
				if(call != null) {
					expr = new MemberCall(expr, call, token);
					continue ;
				}
				
				VariableAccess varAccess = AccessParser.parse(module, sReader, reader);
				if(varAccess != null) {
					expr = new MemberAccess(expr, varAccess, token);
					continue;
				}
			}
			
			if(token.type == TokenType.DOUBLE_DOT) {
				
				reader.skip();
				Expression upper = ExpressionParser.parse(module, sReader, reader, noDecl);
				if(upper == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expected expression for the upper part of a range literal");
				}
				// this is so beautiful it makes me wanna cry
				expr = new RangeLiteral(expr, upper, expr.startToken);
				
			}
			
			if(token.type == TokenType.OPEN_SQUAR) {

				reader.skip();
				Expression index = ExpressionParser.parse(module, sReader, reader, noDecl);
				if(index == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected expression for the index of an array access");
				}
				if(reader.read().type != TokenType.CLOS_SQUAR) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
						"Expected closing bracket to end array access, got "+reader.prev().type+" instead.");
				}
				expr = new ArrayAccess(expr, index, token);
				continue;
				
			}
			
			if(token.type == TokenType.ASSIGN) {
				
				reader.skip();
				Expression rvalue = ExpressionParser.parse(module, sReader, reader, noDecl);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected expression after '='.");
				}
				if(!(expr instanceof Access)) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Attempting to assign to a constant, e.g. "+expr);
				}
				if(token.type == TokenType.ASSIGN) {
					expr = new Assignment(expr, rvalue, token);
				}
				continue;
				
			}
			
			if(token.type == TokenType.AMPERSAND) {
				reader.skip();
				expr = new AddressOf(expr, token);
				continue;
			}
			
			if(token.type == TokenType.QUEST) {
				reader.skip();
				Expression ifTrue = parse(module, sReader, reader, true);
				if(reader.read().type != TokenType.COLON) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected ':' after '?', but got "+reader.prev());
				}
				Expression ifFalse = parse(module, sReader, reader, true);
				expr = new Ternary(expr.startToken, expr, ifTrue, ifFalse);
			}
			
			if(token.type == TokenType.AT) {
				reader.skip();
				expr = new Dereference(expr, token);
				continue;
			}
			
			/** Must find a way to clean that up */
			if(token.type == TokenType.PLUS || token.type == TokenType.STAR
					|| token.type == TokenType.MINUS || token.type == TokenType.SLASH
					|| token.type == TokenType.PERCENT || token.type == TokenType.GREATERTHAN
					|| token.type == TokenType.LESSTHAN || token.type == TokenType.GREATERTHAN_EQUALS
					|| token.type == TokenType.LESSTHAN_EQUALS || token.type == TokenType.EQUALS
					|| token.type == TokenType.NOT_EQUALS || token.type == TokenType.PLUS_ASSIGN
					|| token.type == TokenType.MINUS_ASSIGN || token.type == TokenType.STAR_ASSIGN
					|| token.type == TokenType.SLASH_ASSIGN || token.type == TokenType.DOUBLE_PIPE
					|| token.type == TokenType.DOUBLE_AMPERSAND || token.type == TokenType.PIPE
					|| token.type == TokenType.AMPERSAND || token.type == TokenType.BINARY_AND
					|| token.type == TokenType.CARET) {
				
				reader.skip();
				boolean isLessThan = false;
				boolean isGreaterThan = false;
				boolean isAssign = false;
				if(reader.peek().type == TokenType.ASSIGN) {
					isAssign = true;
					reader.skip();
				} else if(reader.peek().type == TokenType.LESSTHAN) {
					isLessThan = true;
					reader.skip();
				} else if(reader.peek().type == TokenType.GREATERTHAN) {
					isGreaterThan = true;
					reader.skip();
				}
				
				reader.skipWhitespace();
				Expression rvalue = ExpressionParser.parse(module, sReader, reader, noDecl);
				if(rvalue == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expected rvalue after binary operator");
				}
				switch(token.type) {
					case TokenType.PLUS: 
						expr = new Add(expr, rvalue, token); break;
					case TokenType.STAR: 
						expr = new Mul(expr, rvalue, token); break;
					case TokenType.MINUS:
						expr = new Sub(expr, rvalue, token); break;
					case TokenType.SLASH:
						expr = new Div(expr, rvalue, token); break;
					case TokenType.PERCENT:
						expr = new Mod(expr, rvalue, token); break;
					case TokenType.GREATERTHAN:
						if(isGreaterThan) {
							expr = new BinaryCombination(BinaryComp.RSHIFT, expr, rvalue, token); break;
						}
						expr = new Compare(expr, rvalue, CompareType.GREATER, token); break;
					case TokenType.GREATERTHAN_EQUALS: 
						expr = new Compare(expr, rvalue, CompareType.GREATER_OR_EQUAL, token); break;
					case TokenType.LESSTHAN:
						if(isLessThan) {
							expr = new BinaryCombination(BinaryComp.LSHIFT, expr, rvalue, token); break;
						}
						expr = new Compare(expr, rvalue, CompareType.LESSER, token); break;
					case TokenType.LESSTHAN_EQUALS:
						expr = new Compare(expr, rvalue, CompareType.LESSER_OR_EQUAL, token); break;
					case TokenType.EQUALS:
						expr = new Compare(expr, rvalue, CompareType.EQUAL, token); break;
					case TokenType.NOT_EQUALS:
						expr = new Compare(expr, rvalue, CompareType.NOT_EQUAL, token); break;
					case TokenType.PLUS_ASSIGN:
						ensureAccess(expr);
						expr = new Assignment(Mode.ADD, expr, rvalue, token); break;
					case TokenType.MINUS_ASSIGN:
						ensureAccess(expr);
						expr = new Assignment(Mode.SUB, expr, rvalue, token); break;
					case TokenType.STAR_ASSIGN:
						ensureAccess(expr);
						expr = new Assignment(Mode.MUL, expr, rvalue, token); break;
					case TokenType.SLASH_ASSIGN:
						ensureAccess(expr);
						expr = new Assignment(Mode.DIV, expr, rvalue, token); break;
					case TokenType.PIPE:
						if(isAssign) {
							ensureAccess(expr);
							expr = new Assignment(Mode.B_OR, expr, rvalue, token); break;
						}
						expr = new BinaryCombination(BinaryComp.BITWISE_OR, expr, rvalue, token); break;
					case TokenType.AMPERSAND:
					case TokenType.BINARY_AND:
						if(isAssign) {
							ensureAccess(expr);
							expr = new Assignment(Mode.B_AND, expr, rvalue, token); break;
						}
						expr = new BinaryCombination(BinaryComp.BITWISE_AND, expr, rvalue, token); break;
					case TokenType.DOUBLE_PIPE: 
						expr = new BinaryCombination(BinaryComp.LOGICAL_OR,  expr, rvalue, token); break;
					case TokenType.DOUBLE_AMPERSAND:
						expr = new BinaryCombination(BinaryComp.LOGICAL_AND, expr, rvalue, token); break;
					case TokenType.CARET:
						if(isAssign) {
							ensureAccess(expr);
							expr = new Assignment(Mode.B_XOR, expr, rvalue, token); break;
						}
						expr = new BinaryCombination(BinaryComp.BITWISE_XOR, expr, rvalue, token); break;
					default: throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Unknown binary operation yet "+token.type);
				}
				continue;
				
			}
			
			if(token.type == TokenType.AS_KW) {
				
				reader.skip();
				Type type = TypeParser.parse(module, sReader, reader);
				if(type == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.prev()),
							"Expected destination type after 'as' keyword (e.g. for casting)");
				}
				expr = new Cast(expr, type, token);
				continue;
				
			}
			
			return expr;
			
		}
		
		return null;
		
	}
	
	protected static void ensureAccess(Expression expr) {

		if(!(expr instanceof Access)) {
			throw new CompilationFailedError(null, "Trying to assign to a constant :/");
		}
		
	}
	
	protected static Expression parseFlatNoparen(Module module, SourceReader sReader, TokenReader reader, boolean noDecl) {
		
		int mark = reader.mark();
		
		Literal literal = LiteralParser.parse(module, sReader, reader);
		if(literal != null) return literal;

		FunctionCall funcCall = FunctionCallParser.parse(module, sReader, reader);
		if(funcCall != null) return funcCall;
		
		if(!noDecl) {
			Declaration declaration = DeclarationParser.parse(module, sReader, reader);
			if(declaration != null) return declaration;
		}
				
		Access access = AccessParser.parse(module, sReader, reader);
		if(access != null) return access;
		
		Match match = MatchParser.parse(module, sReader, reader);
		if(match != null) return match;
		
		reader.reset(mark);
		return null;
		
	}
	
}
