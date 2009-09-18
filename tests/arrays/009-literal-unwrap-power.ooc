main: func {

	// note: we have to pass the array size because otherwise sizeof() won't work
	print(array := [1, 1, 2, 3, 5, 8, 13, 21, 34, 55], array size)

}

print: func ~intArray (array: Int[], size: Int) {
	
	for(i: Int in 0..size) {
		printf("array[%d] = %d\n", i, array[i])
	}
	
}
