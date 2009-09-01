import text.regexp.RegexpBackend

POSIX: class extends RegexpBackend {
	setPattern: func(pattern: String) {
		this pattern = pattern
	}
	
	matches: func(haystack: String) -> Bool {
		return false
	}
	
	matches: func~withOptions(haystack: String, options: Int) -> Bool {
		return false
	}
}
