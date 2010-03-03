import structs/ArrayList

RegexpBackend: abstract class {
	PCRE = 0, POSIX = 1, DEFAULT_TYPE = 0 : static const Int
	
	pattern: String
	
	setPattern: abstract func(pattern: String, options: Int)
	
	getPattern: func -> String {
		return pattern
	}
	
	getName: abstract func() -> String
	
	matches: abstract func(haystack: String) -> Bool
	matches: abstract func~withOptions(haystack: String, options: Int) -> Bool
    
    getMatches: abstract func(haystack: String) -> Matches
	getMatches: abstract func~withOptions(haystack: String, options: Int) -> Matches
}

Matches: abstract class {
    
    get: abstract func (index: Int) -> String
    
    size: abstract func -> Int
    
    iterator: func -> MatchIterator<String> { MatchIterator<String> new(this) }
        
}

MatchIterator: class <T> extends Iterator<T> {
    
    i := 0
    matches: Matches
    
    init: func ~matchIterator (=matches) {}
    
    hasNext: func -> Bool {
        i < matches size()
    }
    
    next: func -> T {
        c := matches get(i)
        i += 1
        return c
    }
    
    hasPrev: func -> Bool {
        i > 0
    }
    
    prev: func -> T {
        i -= 1
        return matches get(i)
    }
    
    remove: func -> Bool { false }
    
}
