operator + (left, right: String) -> String {
	copy := gc_malloc(left length() + right length() + 1) as String
	memcpy(copy, left, left length())
	memcpy(copy as Char* + left length(), right, right length() + 1) // copy the final '\0'
	copy
}

operator + (left: Int, right: String) -> String {
	left repr() + right
}

operator + (left: String, right: Int) -> String {
	left + right repr()
}

Int: cover from int {

	repr: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%d", this)
		str
	}

}

main: func {

	println ("The answer is " + 42 + ", bitches =D")

}
