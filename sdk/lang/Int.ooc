Int: cover from int {
	repr: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%d", this)
		str
	}
}
