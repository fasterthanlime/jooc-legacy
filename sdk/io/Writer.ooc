/**
 * The writer interface provides a medium-indendant way to write characters
 * to anything.
 */
Writer: abstract class {
	write: abstract func(chars: String, length: SizeT) -> SizeT
        
        write: func ~implicitLength (chars: String) -> SizeT {
            write(chars, chars length())
        }
}
