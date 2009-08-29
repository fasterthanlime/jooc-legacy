include stdio;

/**
 * The writer interface provides a medium-indendant way to write characters
 * to a destination, e.g. a file, a string, a connection, etc.
 */
abstract class Writer {

	Bool closed = false;

	func isClosed -> Bool {
		closed;
	}
	
	func close {
		closeImpl();
		closed = true;
	}
	
	abstract func flush;
	abstract func closeImpl;
	abstract func write(Char c);
	abstract func write(Char[] cbuf, Int off, Int len);
	
	func write(String s) {
		write(s, 0, strlen(s));
	}

}
