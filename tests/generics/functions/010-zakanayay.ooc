Foobar: class {
    yay: func <T> (test: T) {
        test as String println()
    }
}

main: func {
	Foobar new() yay("Yaaay!")
}
