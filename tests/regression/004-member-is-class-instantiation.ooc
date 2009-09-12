A: class {}

B: class {
	
    /* works: */
    a1 = A new() : static const A
	
    /* works too =) */
    a2 := static const A new()
	
	init: func {
		("a1's type is " + a1 class name) println()
		("a2's type is " + a2 class name) println()
	}

}

B new()
