import structs/HashBag

main: func {
    a := HashBag new()
    a add("first", "Hello, HashBag!").add("second", 42)
    a get("first", String) println()
    a remove("second")
    if (a contains("second")) {
        printf("%d\n", a get("second", Int))
    } else {
        "No item found!" println() // <= this one has to be printed
    }
}

