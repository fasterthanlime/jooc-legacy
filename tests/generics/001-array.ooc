Array: class <T> {

	data: T*
	size: SizeT

	new: func (=size) {
		data = gc_malloc(sizeof(T) * size)
	}

	get: func (index: SizeT) -> T {
		data[index]
	}

	set: func (index: SizeT, element: T) {
		data[index] = element
	}

}

main: func {

	println("Creating an array of ints")
	arr = new(10) : Array<Int>
	//arr set(\1..10, 10 - \\)
	for (i in 1..10) arr set(i, 10 - i);
	printf("Array's content = ")
	printf("%d, ", arr get(\1..10))
	println()

	return 0

}
