RegexpBackend: abstract class {
	PCRE = 0, POSIX = 1, DEFAULT_TYPE = 0 : static const Int
	DUMMY_FIELD: const Int = 0
	
	setPattern: abstract func
	match: abstract func -> Bool
}

PCRE: class extends RegexpBackend {
	say: func {
		printf("hi\n");
	}
}


Regexp: class {
	regexpBackend: RegexpBackend
	type: Int
	
	init: func {
		type = RegexpBackend DEFAULT_TYPE
		regexpBackend = PCRE new()
	}
	
	setPattern: func {
		regexpBackend setPattern()
	}
	
	match: func -> Bool {
		return regexpBackend match()
	}
	
	
}

main: func {
	Regexp new()
}
