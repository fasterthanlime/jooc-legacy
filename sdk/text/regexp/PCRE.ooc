import text.regexp.RegexpBackend

use text/regexp/pcre

Pcre: cover from pcre*
pcre_compile: extern func (String, Int, String**, Int*, Pointer) -> Pcre
pcre_exec: extern func(Pcre, Pointer, String, Int, Int, Int, Int*, Int)
pcre_free: extern func(Pointer)

PCRE: class extends RegexpBackend {
	error: String
	errorNum: Int
	re: Pcre
	
	destroy: func {
		pcre_free(re)
	}
	
	setPattern: func(pattern: String) {
		this pattern = pattern
				
		if (! (re = pcre_compile(pattern, 0, error&, errorNum&, null)))
			printf("PCRE compilation failed at expression offset %d: %s\n", errorNum, error)
	}
	
	matches: func(haystack: String) -> Bool {
		// offsets := gc_malloc(10 * sizeof(Int)) as Int*
		return pcre_exec(this re, null, haystack, haystack length(), 0, 0, null, 0) >= 0		
	}
}
