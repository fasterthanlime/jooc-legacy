Array: class <T> {

	data: Pointer
	size: SizeT

	new: func (=size) {
		data = gc_malloc(T size * size)
	}

	get: func (index: SizeT) -> T {
		data[index]
	}

	set: func (index: SizeT, element: T) {
		data[index] = element
	}

}

main: func {

	max := 10
	println("Creating an array of ints")
	arr := new Array<Int> (max)
	for (i : Int in 1..max) arr set(i, max - i);
	printf("Array's content = ")
	for (i: Int in 1..max) printf("%d, ", arr get(i))
	println()

	return 0

}
