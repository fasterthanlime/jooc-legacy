isalnum: extern func(letter: Char)

Char: cover from char {

	isAlphaNumeric: func -> Bool {
		return isalnum(this)
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
