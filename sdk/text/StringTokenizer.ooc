StringTokenizer: class extends Iterable<String> {

    input, delim: String
    index = 0, length: Int

    init: func~withChar(input: String, delim: Char) {
        this~withString(input, String new(delim))
    }
    
    init: func~withString(=input, =delim) {
        T = String // small fix for runtime introspection
        length = input length()
    }
    
    iterator: func -> Iterator<String> { StringTokenizerIterator new(this) }
    
    hasNext: func -> Bool { index < length }
    
    /**
     * @return the next token, or null if we're at the end.
     */
    nextToken: func() -> String {
        // at the end?
        if(!hasNext()) return null
        
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
    hasNext: func -> Bool { st hasNext() }
    next: func -> T       { st nextToken() }
    hasPrev: func -> Bool { false }
    prev: func -> T       { null }
    remove: func -> Bool  { false }
    
}

String: cover from Char* {

    split: func~withString(s:String) -> Iterable<String> {
        StringTokenizer new(this, s)
    }
    
    split: func~withChar(c:Char) -> Iterable<String> {
        StringTokenizer new(this, c)
    }

}
