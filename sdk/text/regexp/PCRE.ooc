import text.regexp.RegexpBackend

PCRE: class extends RegexpBackend {
	error: String
	errorNum: Int
	//reg: pcre@
	
	setPattern: func(pattern: String) {
		this pattern = pattern
		
		//reg = pcre_compile(pattern, 0, error&, errorNum&, null);
	}
	
	matches: func(haystack: String) -> Bool {
		return false;
	}
}
