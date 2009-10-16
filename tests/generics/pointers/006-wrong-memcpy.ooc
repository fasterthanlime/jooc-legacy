Box: class <T> {
    value: T

    init: func (v: T) {
        value = v
    }
}

test: func <T> (T: Class) -> T {
    box := Box new("Hey there!")
    box value as T
}

main: func {
    test(String) println()
	println()
}
