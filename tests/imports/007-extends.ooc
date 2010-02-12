import ordinary into Yay

Yohow: class extends Yay Greeter {
    init: func ~w (=who) {}

    greet: func {
        "Yohow %s!" format(who) println()
    }
}

main: func {
    yo := Yohow new("World")
    yo greet()
}
