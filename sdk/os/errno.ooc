include errno

errno: extern Int
strerror: extern func(Int) -> String

SystemError: class extends Exception {
    init: func ~system {
        super(strerror(errno))
    }
}
