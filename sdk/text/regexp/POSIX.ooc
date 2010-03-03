import text/regexp/RegexpBackend, structs/ArrayList

POSIX: class extends RegexpBackend {
	setPattern: func(pattern: String, options: Int) {
		this pattern = pattern
	}
	
	getName: func -> String { "POSIX" }
	
	matches: func(haystack: String) -> Bool {
		false
	}
	
	matches: func~withOptions(haystack: String, options: Int) -> Bool {
		false
	}
    
    getMatches: func(haystack: String) -> Matches {
        null
    }
    
	getMatches: func~withOptions(haystack: String, options: Int) -> Matches {
        null
    }
    
}
