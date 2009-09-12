Yay: class <T> {
    message: T

    init: func {
		message = gc_malloc(T size)
	}

    printy: func {
        message as String println()
    }

    setMessage: func(=message) {}
}

main: func {

	Yay<String> new() setMessage("Yodel?") .printy()

}
