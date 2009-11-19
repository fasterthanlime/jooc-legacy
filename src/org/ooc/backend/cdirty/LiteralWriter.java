package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.StringLiteral;
import org.ubi.SourceReader;

public class LiteralWriter {

	public static void writeFloat(FloatLiteral floatLiteral, CGenerator cgen) throws IOException {
		cgen.current.app(Double.toString(floatLiteral.getValue()));
	}

	public static void writeString(StringLiteral stringLiteral, CGenerator cgen) throws IOException {
		cgen.current.app('"');
		SourceReader.spelled(stringLiteral.getValue(), cgen.current);
		cgen.current.app('"');
	}

	public static void writeInt(IntLiteral numberLiteral, CGenerator cgen) throws IOException {
		switch(numberLiteral.getFormat()) {
		case HEX:
		case BIN: // C has no binary literals, write it as hex
			cgen.current.app("0x");
			cgen.current.app(numberLiteral.getValue().toString(16));
			break;
		case OCT:
			cgen.current.app('0');
			cgen.current.app(numberLiteral.getValue().toString(8));
			break;
		default:
			cgen.current.app(String.valueOf(numberLiteral.getValue()));
		}
	}

	public static void writeNull(CGenerator cgen) throws IOException {
		cgen.current.app("NULL");		
	}

}
