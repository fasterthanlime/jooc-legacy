import text.regexp.RegexpBackend

use text/regexp/pcre

PcreStruct: cover from pcre
Pcre: cover from PcreStruct*
pcre_compile: extern func (String, Int, String**, Int*, Pointer) -> Pcre

PCRE: class extends RegexpBackend {
	error: String
	errorNum: Int
	reg: Pcre
	
	setPattern: func(pattern: String) {
		this pattern = pattern
		
		reg = pcre_compile(pattern, 0, error&, errorNum&, null);
	}
	
	matches: func(haystack: String) -> Bool {
		return false;
	}
}
