RegexpBackend: abstract class {
	PCRE = 0, POSIX = 1, DEFAULT_TYPE = 0 : static const Int
	
	pattern: String
	
	setPattern: abstract func(pattern: String)
	
	getPattern: func -> String {
		return pattern
	}
	
	matches: abstract func(haystack: String) -> Bool
}