IntArray: cover from Int* {
	
	new: static func(size: SizeT) -> This {
		return gc_calloc(sizeof(Int), size)
	}
	
}

main: func {
	
	max := 20
	array := IntArray new(max)
	for(i: Int in 0..max) array[i] = i
	for(i: Int in 0..max) printf("array[%d] = %d\n", i, array[i])
	
}
