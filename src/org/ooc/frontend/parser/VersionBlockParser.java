package org.ooc.frontend.parser;

import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Statement;
import org.ooc.frontend.model.VersionBlock;
import org.ooc.frontend.model.VersionNodes.VersionAnd;
import org.ooc.frontend.model.VersionNodes.VersionName;
import org.ooc.frontend.model.VersionNodes.VersionNegation;
import org.ooc.frontend.model.VersionNodes.VersionNode;
import org.ooc.frontend.model.VersionNodes.VersionOr;
import org.ooc.frontend.model.VersionNodes.VersionParen;
import org.ooc.frontend.model.interfaces.Versioned;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class VersionBlockParser {

	public static VersionBlock parse(Module module, SourceReader sReader, TokenReader reader) {
		
		int mark = reader.mark();
		
		Token startToken = reader.read();
		if(startToken.type != TokenType.VERSION_KW) {
			reader.reset(mark);
			return null;
		}
		
		if(reader.read().type != TokenType.OPEN_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev()),
				"Expected '(' after version keyword");
		}
		
		VersionNode node = parseVersionNode(module, sReader, reader);
		
		if(reader.read().type != TokenType.CLOS_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev()),
				"Malformed version expression!");
		}
		
		VersionBlock block = new VersionBlock(node, startToken);
		
		fill(module, sReader, reader, block);
		
		return block;
		
	}
	
	public static void fill(Module module, SourceReader sReader,
			TokenReader reader, VersionBlock block) {
		
		if(reader.peek().type == TokenType.OPEN_BRACK) {
			reader.skip();
		} else {
			throw new CompilationFailedError(sReader.getLocation(reader.peek()), "Expected opening brack after version(...)");
		}
		
		NodeList<Include> includes = null;
		while(reader.hasNext() && reader.peek().type != TokenType.CLOS_BRACK) {
			if(reader.skipWhitespace()) continue;
			if(!LineParser.fill(module, sReader, reader, block.getBody())) {
				if(includes == null) includes = new NodeList<Include>();
				if(IncludeParser.fill(sReader, reader, includes)) {
					while(!includes.isEmpty()) {
						Include include = includes.getLast();
						include.setVersion(block);
						module.getIncludes().add(include);
						includes.remove(include);
					}
				} else {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()), "Expected line in code block");
				}
			}
		}
		reader.skip(); // the closing bracket
		
		NodeList<Line> body = block.getBody();
		for(int i = 0; i < body.size(); i++) {
			Line line = body.get(i);
			Statement stmt = line.getStatement();
			if(stmt instanceof Versioned) {
				Versioned vs = (Versioned) stmt;
				vs.setVersion(block);
				System.out.println("Just versioned "+ vs + " to " + block.getVersion());
				
				module.add(stmt);
				body.remove(i);
				i--;
			}
		}
		
	}
	
	
	public static VersionNode parseVersionNode(Module module, SourceReader sReader, TokenReader reader) {
		
		if(reader.peek().type == TokenType.BANG) {
			reader.skip();
			VersionNode inner = parseVersionNode(module, sReader, reader);
			if(inner == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expecting version expression after '!' in version block.");
			}
			return new VersionNegation(inner);
		}
		
		if(reader.peek().type == TokenType.OPEN_PAREN) {
			reader.skip();
			VersionNode inner = parseVersionNode(module, sReader, reader);
			if(inner == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expecting version expression between '()' in version block.");
			}
			if(reader.read().type != TokenType.CLOS_PAREN) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expecting ')', got '"+reader.prev().get(sReader)+"'");
			}
			
			VersionNode node = new VersionParen(inner);
			VersionNode tmp;
			while(true) {
				tmp = getRemain(module, sReader, reader, node);
				if(tmp == null) break;
				node = tmp;
			}
			return node;
		}
		
		String realName = "";
		Token nameTok = reader.peek();
		while(nameTok.type == TokenType.NAME || nameTok.type == TokenType.DEC_INT) {
			reader.skip();
			realName += nameTok.get(sReader);
			nameTok = reader.peek();
		}
		
		if(realName.length() > 0) {
			VersionNode node = new VersionName(realName);
			VersionNode tmp;
			while(true) {
				tmp = getRemain(module, sReader, reader, node);
				if(tmp == null) break;
				node = tmp;
			}
			return node;
		}
		
		return null;
		
	}


	private static VersionNode getRemain(Module module, SourceReader sReader,
			TokenReader reader, VersionNode left) throws CompilationFailedError {
		
		VersionNode ver = null;
		
		while(true) {
			
			Token tok = reader.peek();
			
			if(tok.type == TokenType.DOUBLE_AMPERSAND) {
				reader.skip();
				VersionNode right = parseVersionNode(module, sReader, reader);
				if(right == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expecting version expression after '&&' in version block.");
				}
				ver = new VersionAnd(left, right);
				continue;
			}
			if(tok.type == TokenType.DOUBLE_PIPE) {
				reader.skip();
				VersionNode right = parseVersionNode(module, sReader, reader);
				if(right == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expecting version expression after '||' in version block.");
				}
				ver = new VersionOr(left, right);
				continue;
			}
			
			break;
			
		}
		
		return ver;
	}
	
}
