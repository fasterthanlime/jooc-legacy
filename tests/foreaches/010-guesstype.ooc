import structs/ArrayList

main: func {
    list := ArrayList<String> new() .add("0") .add("1")
    for(s in list) {
        s println()
    }
}
