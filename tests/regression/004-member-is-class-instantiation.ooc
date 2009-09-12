A: class {}

B: class {
    /* works: */
    //a = A new() : static const A
    /* does not work (NullPointerException): */
    //b := /*static*/ /* const */ A new()
	b := A new()
}

