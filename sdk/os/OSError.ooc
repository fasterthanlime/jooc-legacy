include errno

errno: extern Int
strerror: extern func(Int) -> String

OSError: class extends Exception {
    init: func {
        super(strerror(errno))
    }
}
