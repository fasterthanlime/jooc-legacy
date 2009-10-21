import structs/[ArrayList, List]

main: func {
    
    list := [1, 2, 3] as ArrayList
    arr := list toArray() as Int*
    
    for(i in 0..list size()) {
        printf("%d = %d\n", list get(i), arr[i])
    }
    
}
