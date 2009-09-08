Array: class <T> {

	data: T*
	size: SizeT

	init: func (=T, =size) {
		data = gc_malloc(T size * size)
	}
		
	get: func (index: SizeT) -> T {
		return data[index]
	}

	set: func (index: SizeT, element: T) {
		data[index] = element
	}

}

main: func {

	max := 10
	println("Creating an array of ints")
	arr := Array<Int> new(Int, max)
	
	for (i : Int in 0..max) {
		arr set(i, max - i);
	}
	
	printf("Array's content = ")
	isFirst := true
	for (i: Int in 0..max) {
		if(!isFirst) printf(", ")
		isFirst = false
		printf("%d", arr get(i))
	}
	println()
	
	println("Creating an array of chars")
	chars := Array<Char> new(Char, max)
	
	max = 26
	for (i : Int in 0..max) {
		chars set(i, 'a' + i);
	}
	
	printf("Chars's content = ")
	isFirst = true
	for (i: Int in 0..max) {
		if(!isFirst) printf(", ")
		isFirst = false
		printf("%c", chars get(i))
	}
	println()
	
}
