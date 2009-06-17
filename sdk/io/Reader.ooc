/**
 * The reader interface provides a medium-indendant way to read characters
 * from a source, e.g. a file, a string, an URL, etc.
 */
abstract class Reader {

	Long marker;

	abstract func readChar -> Char;
	abstract func hasNext -> Bool;
	abstract func rewind(Int offset);
	abstract func mark -> Long;
	abstract func reset(Long marker);

}

