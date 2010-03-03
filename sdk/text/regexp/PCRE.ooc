import text/regexp/RegexpBackend
use text/regexp/pcre

Pcre: cover from pcre*
pcre_compile: extern func (Char*, Int, const Char**, Int*, Pointer) -> Pcre
pcre_exec: extern func(Pcre, Pointer, Char*, Int, Int, Int, Int*, Int) -> Int
pcre_free: extern func(Pointer)
pcre_copy_substring: extern func (Char*, Int*, Int, Int, Char*, Int) -> Int
pcre_get_substring: extern func (Char*, Int*, Int, Int, const Char**) -> Int

PCRE_DEBUG : Bool = false

PCRE: class extends RegexpBackend {
    CASELESS : extern(PCRE_CASELESS) static const Int
    
    error: String
    errorNum: Int
    re: Pcre
    
    maxMatches := 1024
    
    __destroy__: func {
        if(PCRE_DEBUG) {
            printf("^")
        }
        pcre_free(re)
    }
    
    setMaxMatches: func (=maxMatches) {}
    
    setPattern: func(pattern: String, options: Int) {
        this pattern = pattern
        
        re = pcre_compile(pattern, options, error& as const Char**, errorNum&, null)
        if (! re)
            printf("PCRE compilation failed at expression offset %d: %s\n", errorNum, error)
    }
    
    getName: func -> String { "PCRE" }
    
    matches: func(haystack: String) -> Bool { matches(haystack, 0) }
    
    matches: func~withOptions(haystack: String, options: Int) -> Bool {
        result : Bool = pcre_exec(re, null, haystack, haystack length(), 0, options, null, 0) >= 0
        return result
    }
    
    getMatches: func(haystack: String) -> Matches { getMatches(haystack, 0) }
    
    getMatches: func~withOptions(haystack: String, options: Int) -> Matches {
        offsets := gc_malloc(Int size * maxMatches)
        count := pcre_exec(re, null, haystack, haystack length(), 0, options, offsets, maxMatches)
        
        if(count >= 0) {
            return PcreMatches new(haystack, offsets, count)
        } else {
            return null
        }
    }
    
}

PcreMatches: class extends Matches {
    
    haystack: String
    offsets: Int*
    size: Int
    
    init: func ~pcreMatches (=haystack, =offsets, =size) {}
    
    size: func -> Int { size }
    
    get: func (index: Int) -> String {
        buffer: String
        pcre_get_substring(haystack, offsets, size, index, buffer& as (const Char**))
        result := buffer clone()
        free(buffer)
        result
    }
    
}

