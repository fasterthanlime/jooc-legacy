Int: extern Int

main: func {
	
	max := 20
	array := gc_calloc(sizeof(Int), max) as Int*
	for(i: Int in 0..max) array[i] = i
	for(i: Int in 0..max) printf("array[%d] = %d\n", i, array[i])
	
}
