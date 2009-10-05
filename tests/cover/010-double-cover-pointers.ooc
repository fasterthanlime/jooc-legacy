include ./010-struct

BlahStruct: cover from struct blah {
	i: extern Int
}

Blah: cover from BlahStruct* {
	println: func { this@ i toString() println() }
}

getBlah: func -> Blah {
	return gc_malloc(BlahStruct size) as Blah
}

main: func {
	b : Blah = getBlah()
	b@ i = 42
	b println()
}
