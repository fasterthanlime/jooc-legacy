Test: class <T> {
    message: T

    init: func (msg: String) {
        message = msg
    }

    print: func {
        message as String println()
    }
}

main: func {
    test := Test<String> new("Hello There!")
    test print()
}
