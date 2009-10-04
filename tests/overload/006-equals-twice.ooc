A: class {}

operator == (a, b: A) -> Bool {
    return true
}

main: func {
    a := A new()
    a == a
    (a == a) && (a == a)
}
