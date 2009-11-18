import structs/[ArrayList, List]
// import structs/RangeList

main: func {
    
    list := [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] as ArrayList<Int>
    show(list)
    
}

show: func (list: List<Int>) {
    for(i in list) i toString() println()
}
