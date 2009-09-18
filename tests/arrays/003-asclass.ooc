IntArray: class {
	
	size: SizeT
	data: Int*
	
	init: func(=size) {
		data = gc_calloc(sizeof(Int), size)
	}
	
	get: func(index: Int) -> Int {
		data[index]
	}
	
	set: func(index: Int, value: Int) {
		data[index] = value
	}
	
}

main: func {
	
	max := 20
	array := IntArray new(max)
	for(i: Int in 0..max) array set(i, i)
	for(i: Int in 0..max) printf("array[%d] = %d\n", i, array get(i))
	
}
