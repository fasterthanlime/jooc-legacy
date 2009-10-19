/**
 * The writer interface provides a medium-independent way to write characters
 * to anything.
 */
Writer: abstract class {
	
	close: abstract func()
	
	write: abstract func ~chr (chr: Char)
	
	write: abstract func(chars: String, length: SizeT) -> SizeT
        
	write: func ~implicitLength (chars: String) -> SizeT {
		write(chars, chars length())
	}
}
