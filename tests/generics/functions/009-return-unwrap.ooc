
soho: func <T> (data: T*, index: Int) -> T {

	return data[index]
	
}

main: func {
	
	/*
	data := [1, 2, 3]
	for (i in 0..3) printf("data[%d] = %d\n", i, data[i])
	
	for (i in 0..3) {
		value := get(data, i)
		printf("data[%d] = %d\n", i, value)
	}
	*/
	
	data := [1, 2, 3]
	value := soho(data, 1)
	
}
