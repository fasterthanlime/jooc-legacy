import lang/[stdio, Range]

LLong: cover from long long {
	
	toString: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%d", this)
		str
	}
	
	isOdd:  func -> Bool { this % 2 == 1 }
	isEven: func -> Bool { this % 2 == 0 }
	
	in: func(range: Range) -> Bool {
		return this >= range min && this < range max
	}
	
}
