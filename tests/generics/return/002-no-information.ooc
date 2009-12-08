// See http://github.com/nddrylliog/ooc/issues#issue/44

Cell: class <T> {
    
    data: T
    
    init: func (=data) {}
    
}


main: func {
	
    cell := Cell<Int> new(42)
    value := cell data
    
}
