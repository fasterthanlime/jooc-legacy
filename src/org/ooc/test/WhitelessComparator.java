package org.ooc.test;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class WhitelessComparator {

	protected static class WhitelessReader {
		
		Reader reader;

		public WhitelessReader(Reader reader) {
			this.reader = reader;
		}
		
		public int read() throws IOException {
			
			int c;
			while(true) {
				c = reader.read();
				if(c == -1 || !Character.isWhitespace(c)) {
					return c;
				}
			}
			
		}
		
	}
	
	public boolean compare(File file, File file2) throws IOException {

		boolean same = true;
		
		try {
			
			WhitelessReader wr1 = new WhitelessReader(new FileReader(file));
			WhitelessReader wr2 = new WhitelessReader(new FileReader(file2));
			int c1, c2;
			while((c1 = wr1.read()) != -1 && (c2 = wr2.read()) != -1) {
				if(c1 != c2) {
					same = false;
					break;
				}
			}
			if(wr1.read() != -1 || wr2.read() != -1) {
				same = false; // Not the same length
			}
			
		} catch(EOFException e) {
			// Huh. Finished yet? Great.
		}
		
		return same;
		
	}

}
