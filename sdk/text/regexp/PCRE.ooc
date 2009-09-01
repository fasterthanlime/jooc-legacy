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
		
		re = pcre_compile(pattern, 0, error&, errorNum&, null);
		
		if (! re)
			printf("PCRE compliation failed at expression offset %d: %s\n", errorNum, error)
	}
	
	matches: func(haystack: String) -> Bool {
		return pcre_exec(re, null, haystack, haystack length(), 0, 0, null, 10) > 0
	}
}
