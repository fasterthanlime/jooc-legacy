include ctype

isalnum: extern func(letter: Char) -> Int
isalnum: extern func(letter: Char) -> Int
isspace: extern func(letter: Char) -> Int
tolower: extern func(letter: Char) -> Char

Char: cover from char {

	isAlphaNumeric: func -> Bool {
		return isalnum(this)
	}
	
	isAlpha: func -> Bool { 
		return isalpha(this)
	}
	
	isWhitespace: func() -> Bool {
		return isspace(this)
	}

	toLower: func() -> Char {
		return tolower(this)
	}

	toInt: func -> Int {
		if ((this >= 48) && (this <= 57)) {
			return (this - 48)
		}
		return -1
	}
	
	print: func { 
		printf("%c", this)
	}
	
	println: func {
		printf("%c\n", this)
	}
}
