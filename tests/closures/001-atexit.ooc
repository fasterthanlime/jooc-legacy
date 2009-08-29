extern func atexit (Func)

func main {

	atexit(func {
		printf("Goodbye, world :(\n")
	})

	printf("Hello, world ;)\n")

}
