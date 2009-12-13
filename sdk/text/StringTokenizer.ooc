import structs/ArrayList

StringTokenizer: class extends Iterable<String> {

    input, delim: String
    index = 0, length, maxSplits, splits: Int

    init: func~withCharWithoutMaxSplits(input: String, delim: Char) {
        this~withChar(input, delim, -1)
    }

    init: func~withStringWithoutMaxSplits(input: String, delim: String) {
        this~withString(input, delim, -1)
    }

    init: func~withChar(input: String, delim: Char, maxSplits: Int) {
        this~withString(input, String new(delim), maxSplits)
    }
    
    init: func~withString(=input, =delim, =maxSplits) {
        T = String // small fix for runtime introspection
        length = input length()
        splits = 0
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

        // maximal count of splits done?
        if(splits == maxSplits) {
            index = length
            return input substring(oldIndex)
        }
         
        // skip all non-delimiters
        while(hasNext() && !delim contains(input[index])) index += 1
        
        splits += 1
        return input substring(oldIndex, index)
    }

    /**
     * @return the tokens as ArrayList<String>
     */
    toArrayList: func -> ArrayList<String> {
        result := ArrayList<String> new()
        for(token: String in this) {
            result add(token)
        }
        result
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

    split: func~withString(s: String, maxSplits: Int) -> StringTokenizer {
        StringTokenizer new(this, s, maxSplits)
    }
    
    split: func~withChar(c: Char, maxSplits: Int) -> StringTokenizer {
        StringTokenizer new(this, c, maxSplits)
    }

    split: func~withStringWithoutMaxSplits(s: String) -> StringTokenizer {
        StringTokenizer new(this, s)
    }

    split: func~withCharWithoutMaxSplits(c: Char) -> StringTokenizer {
        StringTokenizer new(this, c)
    }
}
