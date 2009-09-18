main: func -> Int {

	test = "A short list of long stories" : String
	start = "A short list" : String
	end = "long stories" : String
	
	printf("String '%s' startsWith '%s'? ", test, start)
	if (test startsWith(start)) {
		printf("OK\n")
	} else {
		printf("FAIL\n")
	}
	
	printf("String '%s' endsWith '%s'? ", test, end)
	if (test endsWith(end)) {
		printf("OK\n")
	} else {
		printf("FAIL\n")
	}
	
	printf("Substring of '%s' from [2]: %s\n", test, test substring(2))
	
	printf("Reversal of '%s': %s\n", test, test reverse())
	
	return 0
}
