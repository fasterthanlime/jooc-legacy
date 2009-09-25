import lang/stdio

Double: cover from double {
	
	toString: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%.2f", this)
		str
	}
	
	abs: func -> This {
		return this < 0 ? -this : this
	}
	
}
