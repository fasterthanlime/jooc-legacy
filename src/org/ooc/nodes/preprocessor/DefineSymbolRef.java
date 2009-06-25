package org.ooc.nodes.preprocessor;

import java.io.EOFException;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.numeric.BooleanLiteral;
import org.ooc.nodes.numeric.NumberLiteral;
import org.ooc.nodes.others.NullLiteral;
import org.ooc.nodes.others.RawCode;
import org.ooc.nodes.text.StringLiteral;
import org.ooc.nodes.types.Type;
import org.ooc.parsers.BooleanLiteralParser;
import org.ooc.parsers.NullLiteralParser;
import org.ooc.parsers.NumberLiteralParser;
import org.ooc.parsers.StringLiteralParser;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * Reference to a define symbol
 * 
 * @author Amos Wenger
 */
public class DefineSymbolRef extends RawCode implements Typed {

	private final Define defineSymbol;
	private Type type;
	
	/**
	 * Default constructor
	 * @param location
	 * @param defineSymbol
	 */
	public DefineSymbolRef(FileLocation location, Define defineSymbol) {
		super(location, defineSymbol.name);
		this.defineSymbol = defineSymbol;
		type = Type.UNKNOWN;
	}
	
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		resolve();
		lock();
		
	}

	private void resolve() {
		
		SourceReader reader = SourceReader.getReaderFromText(location.toString()+":#"+content, defineSymbol.content);
		
		try {
			
			reader.skipWhitespace();
			int mark = reader.mark();
			
			reader.reset(mark);
			NumberLiteral num = NumberLiteralParser.readNumberLiteral(reader);
			if(num != null) {
				type = num.getType();
				return;
			}
			
			reader.reset(mark);
			StringLiteral str = StringLiteralParser.readStringLiteral(reader);
			if(str != null) {
				type = str.getType();
				return;
			}
			
			reader.reset(mark);
			BooleanLiteral bool = BooleanLiteralParser.readBooleanLiteral(reader);
			if(bool != null) {
				type = bool.getType();
				return;
			}
			
			reader.reset(mark);
			NullLiteral nul = NullLiteralParser.readNullLiteral(reader);
			if(nul != null) {
				type = nul.getType();
				return;
			}
			
		} catch(SyntaxError e) {
			
			throw new CompilationFailedError(e);
			
		} catch(EOFException e) {
			
			// Well, too bad, it'll stay a mystery.
			
		}
		
	}

	
	public Type getType() {
		
		return type;
		
	}

	
}
