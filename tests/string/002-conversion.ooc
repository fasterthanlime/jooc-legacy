main: func -> Int {
	
	number = "12345" : String
	printf("Number as integer: %d\n", number toInt())
	printf("Number as long: %ld\n", number toLong())
	printf("Number as long long: %lld\n", number toLLong())
	anotherNumber = "12345.6789" : String
	printf("Another number as double: %f\n", anotherNumber toDouble())
	return 0
	
}
