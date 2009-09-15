Int: cover from int {
	repr: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%d", this)
		str
	}
	
	isOdd:  func -> Bool { this % 2 == 1 }
	isEven: func -> Bool { this % 2 == 0 }
}
