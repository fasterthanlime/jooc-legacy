import structs/ArrayList

/*
 
 Regression test for bug #64:
 http://github.com/nddrylliog/ooc/issues#issue/64
 
 It should print "One", "One" for correct behavior.
 Otherwise, it prints "One", "Three"
  
 */
C: class {
    name: String
    names: ArrayList<String>

    init: func (=name, =names) {
        
    }

    test: func {
        for(name in names) {
        }
    }
}

main: func {
    c := C new("One", ["Two", "Three"] as ArrayList<String>)
    c name println()
    c test()
    c name println()
}

