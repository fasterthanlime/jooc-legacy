package org.ooc.backend.cdirty;

import java.io.IOException;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.VariableAccess;

public class AccessWriter {

	public static void write(Access access, CGenerator cgen) throws IOException {
		write(access, true, cgen, 0);
	}
	
	public static void write(Access access, boolean doTypeParams, CGenerator cgen) throws IOException {
		write(access, doTypeParams, cgen, 0);
	}
	
	public static void write(Access access, boolean doTypeParams, CGenerator cgen, int refOffset) throws IOException {
		if(access instanceof ArrayAccess) {
			ArrayAccessWriter.write((ArrayAccess) access, cgen);
		} else if(access instanceof MemberAccess) {
			MemberAccessWriter.write((MemberAccess) access, doTypeParams, cgen, refOffset);
		} else {
			LocalAccessWriter.write((VariableAccess) access, doTypeParams, cgen, refOffset);
		}
	}
	
}
