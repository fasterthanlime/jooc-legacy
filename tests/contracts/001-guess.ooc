main: func {

	number : Int
	while (true) {
		println("Give me a number between 0 and 100 (42 exits)\n")
		scanf("%d", number&)
		if(check(number)) break
	}
	println("You got 42 =)")

}

check: func (number: Int) -> Bool {

	require (0 <= number < 100)
	
	return number == 42

}

rand: func -> Int {

	srand(ctime(null))
	return rand % 100;
	
	ensure (0 <= returned < 100)

}
