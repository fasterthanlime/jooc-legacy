Test: class <T> {
    message: T

    set: func (msg: String) {
        message = msg
    }

    print: func {
        message as String println()
    }
}

main: func {
    test := Test<String> new()
    test set("Hello There!")
    test print()
}

