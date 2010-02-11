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

pipapo: func (hello: String) -> Int {
    hello println()
    42
}
