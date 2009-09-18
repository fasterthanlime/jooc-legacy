main: func {
	
	max := 20
	array := gc_calloc(Int size, max) as Int*
	for(i in 0..max) array[i] = i
	for(i in 0..max) printf("array[%d] = %d\n", i, array[i])
	
}
