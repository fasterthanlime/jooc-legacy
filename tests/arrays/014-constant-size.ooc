
// Don't really need to do anything to see the error
main: func {
}

// Adding 'static', or removing 'const', the error will still occur
MAX_SIZE: const Int = 100
Wakka: class {
    init: func() {}
    somearray: Int[MAX_SIZE]
}
