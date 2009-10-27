atexit: extern func (Func)

main: func {

	message := "Goodbye, world :(\n"

	atexit(func {
		printf(message)
	})

	printf("Hello, world ;)\n")

}
