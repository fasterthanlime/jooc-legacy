import structs/Bag

main: func {
    a := Bag new()
    a add("Hello, Bag!").add(27).add(Bag new()) // Fill bag with various items
    a get(0, String) println() // You have to specify the class-type when retrieving
    printf("%d\n", a get(1, Int))
    b := a get(2, Bag)
    b add(42)
    b removeAt(0, Bag)
}

    
