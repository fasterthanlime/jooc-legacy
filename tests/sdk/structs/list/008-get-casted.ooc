import structs/[List, ArrayList]

main: func {
    
    list := [1, 2, 3] as ArrayList<Int>
    print(list)
    
}

print: func <T> (list: List<T>) {
    
    val := list get(0) as Int
    val toString() println()
    
}
