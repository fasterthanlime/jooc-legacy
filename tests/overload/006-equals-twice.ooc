A: class {}

operator != (a, b: A) -> Bool {
	// obviously incorrect, it's just to test that overloading works correctly
    return true
}

main: func {
    a := A new()
    if (a != a) println("Yay!")
    if (a != a && a != a) println("Yay^2!")
}
