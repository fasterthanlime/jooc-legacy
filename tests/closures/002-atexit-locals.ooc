extern func atexit (Func)

func main {

	String message = "Goodbye, world :(\n"

	atexit(func {
		printf(message)
	})

	printf("Hello, world ;)\n")

}
