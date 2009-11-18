import structs/[ArrayList]

main: func {

    arr := [1, 2, 3] as ArrayList<Int>
    
    for(i in arr) {
        i as String println()
    }
    println()
    
    for(i in [1, 2, 3] as ArrayList<Int>) {
        i as String println()
    }
    println()
    
    for(i in [1, 2, 3, 4, 5]) {
        i as String println()
    }
    println()
    
}
