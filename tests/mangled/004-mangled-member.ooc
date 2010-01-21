Greeter: class {
    message: String

    init: func (=message) {
    }

    greet: mangled(huhu) func {
        message println()
    }
}

huhu: extern func(Greeter)

main: func {
    g := Greeter new("Hello World!")
    huhu(g)
}
