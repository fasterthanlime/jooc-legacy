id: func <T> (t: T) -> T { return t }

main: func {

	a := 42
	b := id(a)
	c := id(42)
	printf("%d, %d, %d, %d, and %d are the.. same!\n", a, b, c, id(a), id(42));

}
