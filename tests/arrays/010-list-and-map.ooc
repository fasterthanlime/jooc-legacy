import structs/ArrayList

main: func {
    
    "high-level test" println()
    for(i in [1, 2, 3] as ArrayList) {
        i toString() println()
    }
    
    //"high-level test #2" println()
    //for(i in [1, 2, 3]) {
        //i toString() println()
    //}
    
    
    ["toto" => 18, "maman" => 42]
    
    "low-level test" println()
    arr := [1, 2, 3]
    list := ArrayList<Int> new(arr, arr size)
    
    // I love this =)
    printList := func (list: ArrayList<Int>) {
        for(i in list) {
            printf("%d\n", i)
        }
    }
    printList(list);
    
    "adding 4 and printing again" println()
    list += 4
    printList(list);
    
}


