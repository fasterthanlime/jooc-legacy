/**
 * The reader interface provides a medium-indendant way to read characters
 * from a source, e.g. a file, a string, an URL, etc.
 */
Reader: abstract class {
	marker: Long
	
	read: abstract func(chars: String, offset: Int, count: Int)
	readChar: abstract func() -> Char
	hasNext: abstract func() -> Bool
	rewind: abstract func(offset: Int)
	mark: abstract func() -> Long
	reset: abstract func(marker: Long)
}