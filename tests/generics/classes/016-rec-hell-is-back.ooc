import structs/[ArrayList, List]

Unit: class {
    
    name : String
    
    init: func (=name) {}
    
}

Nav: class <T> {
    
    unit: T
    
    init: func (=unit) {}
    
}

main: func {
    
    list := ArrayList<Nav<Unit>> new()
    list add(Nav<Unit> new(Unit new("Dragoon")))
    unit: getUnit(list, 0)
    "Got unit %s\m" format(unit name) println()
    
}

getUnit: func <T> (list: List<Nav<T>>, index: Int) -> T {
    
    return list get(index) unit
    
}

