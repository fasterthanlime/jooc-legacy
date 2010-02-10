yo := "YO!"

helloWorld: func {
    "Hello World!" println()
}

Greeter: class {
    who: String

    init: func (=who) {
    }

    greet: func {
        "Hello %s!" format(who) println()
    }
}
