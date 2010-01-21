Greeter: class {
    message: String

    init: func (=message) {
    }

    greet: mangled func {
        message println()
    }
}

greet: extern func(Greeter)

main: func {
    g := Greeter new("Hello World!")
    g greet()
    greet(g)
}
