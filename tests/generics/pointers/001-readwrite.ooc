Container: class <T> {

	data: T*
	
	init: func (=T, size: SizeT) {
		data = gc_malloc(size)
	}
	
	get: func (index: Int) -> T {
		return data[index]
	}
	
	get2: func (index: Int) -> T {
		element : T
		element = data[index]
		return element
	}
	
	get3: func (index: Int) -> T {
		element := data[index]
		return element
	}
	
	set: func (index: Int, element: T) {
		data[index] = element
	}
	
	set2: func (index: Int, element: T) {
		dst := data + (index * T size)
		memcpy(dst&, element&, T size)
	}
	
	equals: func (index: Int, element: T) -> Bool {
		candidate : T
		candidate = data[index] /* as Int */
		printf("data[%d] == %d\n", index, candidate)
		return candidate == element
	}

}

main: func {

	cont := Container<Int> new(Int, 10)
	cont set(2, 42)
	cont set2(3, 24)
	printf("cont get(2)  = %d\n", cont get(2))
	printf("cont get(3)  = %d\n", cont get(3))
	printf("cont get2(2) = %d\n", cont get2(2))
	printf("cont get2(3) = %d\n", cont get2(3))
	printf("cont equals(2, 42) ? %s\n", cont equals(2, 42) repr())

}
