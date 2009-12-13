import structs/[Array, ArrayList]

main: func {

    list := ["HO", "YLLAER", "?"] as ArrayList<String>
    for(s in list) {
        s = s reverse()
        for(c in s) {
            c toLower() print()
        }
        " " print()
    }
    println()

}
