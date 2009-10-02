id: func <T> (t: T) -> T { return t }

main: func {

	a := 42
	b := id(42)
	c := id(a)
	d := id(b)
	e := id(c)
	printf("%d, %d, %d, %d, %d, %d and %d are the.. same!\n", a, b, c, d, e, id(a), id(42));

}
