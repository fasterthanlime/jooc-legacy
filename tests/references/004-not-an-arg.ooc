Int: cover {
	
	print: func { printf("number = %d", this) }
	println: func { printf("number = %d\n", this) }
	
}

String: cover {
	
	print: func { printf("%s", this) }
	println: func { printf("%s\n", this) }
	
}

main: func {

	number := 32
	ref := number& as Int@
	number println()
	
	"adding 3 to number" println()
	(number += 3) println()
	
	"adding 5 to ref" println()
	(ref += 5) println()
	
	"adding 7 to number&" println()
	add(number&, 7)
	number println()
	
	"adding 9 to ref" println()
	add(ref&, 7)
	number println()

}

// receive a regular pointer to int, treat is as such (e.g. you must dereference yourself)
add: func (dst: Int@, off: Int) {
	// in C: (*dst) += off
	dst += off
}
