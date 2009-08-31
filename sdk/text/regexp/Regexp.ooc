RegexpBackend: abstract class {
	PCRE = 0, POSIX = 1, DEFAULT_TYPE = 0 : static const Int
	
	pattern: String
	
	setPattern: abstract func(pattern: String)
	
	getPattern: func -> String {
		return pattern
	}
	
	match: abstract func -> Bool
}

PCRE: class extends RegexpBackend {
	setPattern: func(pattern: String) {
		this pattern = pattern
	}
}

POSIX: class extends RegexpBackend {
	setPattern: func(pattern: String) {
		this pattern = pattern
	}
}


Regexp: class {
	regexpBackend: RegexpBackend
	type: Int

	
	init: func ~withType(=type) {
		setup()
	}
	
	init: func ~withPattern(pattern: String) {
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
	
	match: func -> Bool {
		return regexpBackend match()
	}

	getEngine: func -> Int {
		return type
	}
	
}

main: func {
	rx := Regexp new()
	rx setPattern("Test")
	printf("Engine: %d\n", rx getEngine());
	printf("Pattern: %s\n", rx getPattern());
}
