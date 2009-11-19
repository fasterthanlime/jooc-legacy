package org.ooc.frontend.parser;

import java.math.BigInteger;

import org.ooc.frontend.model.ArrayLiteral;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Literal;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

public class LiteralParser {

	public static Literal parse(Module module, SourceReader sReader, TokenReader reader) {

		int mark = reader.mark();
		
		Token token = reader.read();
		if(token.type == TokenType.STRING_LIT) {
			return new StringLiteral(token.get(sReader), token);
		}
		if(token.type == TokenType.CHAR_LIT) {
			try {
				return new CharLiteral(SourceReader.parseCharLiteral(token.get(sReader)), token);
			} catch (SyntaxError e) {
				throw new CompilationFailedError(sReader.getLocation(token), e.getMessage());
			}
		}
		if(token.type == TokenType.DEC_INT) 
			return new IntLiteral(new BigInteger(token.get(sReader)
					.replace("_", "")), Format.DEC, token);
		if(token.type == TokenType.HEX_INT)
			return new IntLiteral(new BigInteger(token.get(sReader)
					.replace("_", "").toUpperCase(), 16), Format.HEX, token);
		if(token.type == TokenType.OCT_INT)
			return new IntLiteral(new BigInteger(token.get(sReader)
					.replace("_", "").toUpperCase(), 8), Format.OCT, token);
		if(token.type == TokenType.BIN_INT)
			return new IntLiteral(new BigInteger(token.get(sReader)
					.replace("_", "").toUpperCase(), 2), Format.BIN, token);
		if(token.type == TokenType.DEC_FLOAT)
			return new FloatLiteral(Double.parseDouble(token.get(sReader)
					.replace("_", "")), token);
		if(token.type == TokenType.TRUE)
			return new BoolLiteral(true, token);
		if(token.type == TokenType.FALSE)
			return new BoolLiteral(false, token);
		if(token.type == TokenType.NULL)
			return new NullLiteral(token);
		if(token.type == TokenType.OPEN_SQUAR) {
			ArrayLiteral arrayLiteral = new ArrayLiteral(token);
			reader.rewind();
			if(!ExpressionListFiller.fill(module, sReader, reader, arrayLiteral.getElements(),
					TokenType.OPEN_SQUAR, TokenType.CLOS_SQUAR)) {
				throw new CompilationFailedError(null, "Malformed array literal");
			}
			return arrayLiteral;
		}
			
		
		reader.reset(mark);
		return null;
		
	}
	
}
