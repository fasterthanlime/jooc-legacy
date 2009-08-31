import text.regexp.RegexpBackend

POSIX: class extends RegexpBackend {
	setPattern: func(pattern: String) {
		this pattern = pattern
	}
	
	matches: func(haystack: String) -> Bool {
		return false;
	}
}
