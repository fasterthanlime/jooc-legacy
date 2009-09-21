Char: cover from char {

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
