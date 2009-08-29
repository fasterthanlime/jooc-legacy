main: func {

	number := 32
	printf("number = %d\n", number)
	printf("adding 3\n")
	add(number&, 3)
	printf("number = %d\n", number)

}

// receive a regular pointer to int, treat is as such (e.g. you must dereference yourself)
add: func (dst: Int*, off: Int) {
	// in C: (*dst) += off
	dst@ += off
}
