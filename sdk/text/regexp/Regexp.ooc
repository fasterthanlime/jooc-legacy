RegexpBackend: abstract class {
	PCRE: const Int = 0
	POSIX: const Int = 1
	DEFAULT_TYPE: const Int = 0
	
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
	
	new: func {
		type = RegexpBackend DEFAULT_TYPE
		regexpBackend = new PCRE
	}
	
	setPattern: func {
		regexpBackend setPattern()
	}
	
	match: func -> Bool {
		return regexpBackend match()
	}
	
	
}

main: func {
	new Regexp;
}
