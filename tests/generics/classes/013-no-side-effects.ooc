Test: class <T> {
    message: T

    set: func (msg: Int) {
        message = msg
    }

    print: func {
		printf("Value = %d\n", message as Int)
    }
}

main: func {
    test := Test<Int> new()
	i := 42
    test set(i)
    test print()
	printf("Changing original value to 35..\n")
	i = 35
	test print()
}

