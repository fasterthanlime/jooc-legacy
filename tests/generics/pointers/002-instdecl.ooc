Yay: class <T> {
    message: T

    init: func(=T) {
		message = gc_malloc(T size)
	}

    printy: func {
        message as String println()
    }

    setMessage: func(=message) {}
}

main: func {

	Yay new(String) setMessage("Yodel?") .printy()

}
