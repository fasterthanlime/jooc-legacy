include stdio, locale;
import io.Writer;

class Scribe {

	const static Int BUFFER_SIZE = 4096;
	
	Writer writer;
	String buffer;
	
	new(=writer) {
		buffer = malloc(BUFFER_SIZE);
		setlocale(LC_ALL, "C"); // What a *HACK* !
	}
	
	func write(Char c) {
		writer.write(c);
	}
	
	func write(String s) {
		writer.write(s);
	}
	
	func write(UInt u) {
		sprintf(buffer, "%u", u);
		writer.write(buffer);
	}
	
	func write(Int i) {
		sprintf(buffer, "%d", i);
		writer.write(buffer);
	}
	
	func write(Float f) {
		sprintf(buffer, "%f", f);
		writer.write(buffer);
	}
	
	func write(Bool b) {
		if(b) {
			writer.write('1');
		} else {
			writer.write('0');
		}
	}
	
	/**
	 * Delegate for writer.close()
	 */
	func close {
		writer.close();
	}

}
