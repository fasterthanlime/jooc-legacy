A: class {
    test: func <T> (data: T) {
		data as Int toString() println()
    }

    foo: func {
        test(123)
    }
}

main: func {
    a := A new()
    a foo()
}
