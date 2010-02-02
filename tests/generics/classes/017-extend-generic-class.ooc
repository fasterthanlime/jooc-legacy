import structs/HashMap

MyMap: class extends HashMap<Int> {
    
    // the suffix is needed in j/ooc.. lame =)
    init: func ~mymap {
        init(100)
    }
    
    init: func ~mymapWithCapacity (capacity: UInt) {
        // in j/ooc, we need to assign the generic types that we 'remove'
        // when extending the class. Otherwise, crash guaranteed.
        // it would be desirable for future compilers to deduce that
        // from the extends Blah<Int> clause
        T = Int
        super(capacity)
    }
    
}

main: func {
    
    mm := MyMap new()
    
    // TMTOWTDI o/
    for(i in 1..4)
        mm put("%d" format(i), i)
        
    for(i in mm)
        "%d" format(i) println()
}
