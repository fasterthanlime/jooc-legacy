package org.ooc.frontend.parser;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.VersionBlock;
import org.ooc.frontend.model.VersionNodes.VersionName;
import org.ooc.frontend.model.VersionNodes.VersionNegation;
import org.ooc.frontend.model.VersionNodes.VersionNode;
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
		
		VersionNode node = parseVersionNode(module, sReader, reader);
		VersionBlock block = new VersionBlock(null, startToken);
		
		if(reader.read().type != TokenType.OPEN_PAREN) {
			throw new CompilationFailedError(sReader.getLocation(reader.prev()),
				"Expected opening parenthesis after version keyword");
		}
		
		
		reader.skip();
		
		ControlStatementFiller.fill(module, sReader, reader, block.getBody());
		
		return block;
		
	}
	
	
	public static VersionNode parseVersionNode(Module module, SourceReader sReader, TokenReader reader) {
		
		if(reader.peek().type == TokenType.BANG) {
			reader.skip();
			VersionNode inner = parseVersionNode(module, sReader, reader);
			if(inner == null) {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Expecting version name after '!' in version block.");
			}
			return new VersionNegation(inner);
		}
		
		Token nameTok = reader.read();
		if(nameTok.isNameToken()) {
			VersionNode ver = new VersionName(nameTok.get(sReader));
			return ver;
		}
		
		return null;
		
	}
	
}
