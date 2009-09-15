Double: cover from double {
	repr: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%.2f", this)
		str
	}
}
