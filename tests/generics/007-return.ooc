
get: func <T> (data: T*, index: Int) -> T {

	return data[index]
	
}

main: func {
	
	data := [1 as Int, 2, 3]
	for (i : Int in 0..3) printf("data[%d] = %d\n", i, data[i])
	
	for (i : Int in 0..3) {
		value : Int
		value = get(data, i)
		printf("data[%d] = %d\n", i, value)
	}
	
}
