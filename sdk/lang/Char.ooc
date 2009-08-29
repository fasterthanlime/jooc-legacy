Char: cover from char {

	toInt: func -> Int {
		if ((this >= 48) && (this <= 57)) {
			return (this - 48)
		}
		return -1
	}
	
}
