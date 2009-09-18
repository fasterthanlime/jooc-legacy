main: func {

	// note: we have to pass the array size because otherwise sizeof() won't work
	print([1, 1, 2, 3, 5, 8, 13, 21, 34, 55], 10)

}

print: func ~intArray (array: Int[], size: Int) {
	
	for(i in 0..size) {
		printf("array[%d] = %d\n", i, array[i])
	}
	
}
