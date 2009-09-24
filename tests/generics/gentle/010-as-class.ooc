A: class {
    f: func <T> (T: Class) -> T {
        return 1
    }
}

main: func {
	a := A new()
	a f(Int)
}

