atexit: extern func (Func)

main: func {

	atexit(func {
		printf("Goodbye, world :(\n")
	})

	printf("Hello, world ;)\n")

}
