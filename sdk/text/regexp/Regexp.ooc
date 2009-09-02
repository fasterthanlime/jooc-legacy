import text.regexp.RegexpBackend
import text.regexp.PCRE
import text.regexp.POSIX

Regexp: class {
	regexpBackend: RegexpBackend
	type: Int

	
	init: func~withType(=type) {
		setup()
	}
	
	init: func~withPattern(pattern: String) {
		this()
		setPattern(pattern)
	}
	
	init: func {
		type = RegexpBackend DEFAULT_TYPE
		setup()
	}
	
	setup: func {
		if (type == RegexpBackend PCRE)
			regexpBackend = PCRE new()
		else if (type == RegexpBackend POSIX)
			regexpBackend = POSIX new()
	}
	
	setPattern: func(pattern: String) {
		regexpBackend setPattern(pattern)
	}
	
	getPattern: func -> String {
		return regexpBackend getPattern()
	}
	
	matches: func(haystack: String) -> Bool {
		return regexpBackend matches(haystack)
	}
	
	matches: func~withOptions(haystack: String, options: Int) -> Bool {
		return regexpBackend matches(haystack, options)
	}

	getEngine: func -> Int { type }
	
	getEngineName: func -> String { regexpBackend getName() }
	
}

main: func {
	rx := Regexp new~withPattern("^Hello \\w+$")
	printf("Engine: %d\n", rx getEngine());
	printf("Pattern: %s\n", rx getPattern());
	
	if (rx matches("Hello world", /* PCRE CASELESS */ 0))
		printf("Matches!\n")
	else
		printf("No match\n")
		
	// if (string =~ /^Hello, (\w+)/i)
		
}

