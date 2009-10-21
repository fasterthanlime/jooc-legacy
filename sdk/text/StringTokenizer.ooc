StringTokenizer: class extends Iterable<String> {
	input: String
	delim: String
    
	index := 0
    length : Int

	init: func~withChr(input: String, delim: Char) {
		this~withString(input, delim& as String)
	}
	
	init: func~withString(=input, =delim) {
        T = String // small fix for runtime introspection
        length = input length()
    }
	
	iterator: func() -> Iterator<String> {
		return StringTokenizerIterator new(this)
	}
    	
	hasNext: func() -> Bool {
		return index < length
	}
	
    /**
     * @return the next token, or null if we're at the end.
     */
	nextToken: func() -> String {
        // at the end?
        if(!hasNext()) return null;
        
        // skip all delimiters
        while(hasNext() && delim contains(input[index])) index += 1
        
        // save the index
		oldIndex := index
        
        // skip all non-delimiters
        while(hasNext() && !delim contains(input[index])) index += 1
        
		return input substring(oldIndex, index)
	}
}

StringTokenizerIterator: class <T> extends Iterator<T> {

	st: StringTokenizer
	index := 0
	
	init: func(=st) {}
	
	hasNext: func -> Bool {
		return st hasNext()
	}
	
	next: func -> T {
		return st nextToken()
	}
	
}
