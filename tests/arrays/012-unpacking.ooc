
Time: class {

    hour, min, sec : Int
    
    init: func (=hour =min, =sec) {}
    
    get: static func -> This {
        new Time(9, 12, 35)
    }

}

main: func {

    // long form with current syntax
    {
        time := Time get()
        h := time hour
        m := time minute
        s := time second
    }
    
    // short form with object-unpacking syntax, type inference, same-names
    {
        [hour, min, sec] := Time get()
    }
    
    // short form with object-unpacking syntax and type inference
    {
        [hour => h, min => m, sec => s] := Time get()
    }

    // declaring beforehand, then mass assignment
    {
        h, m, s: Int
        [hour => h, min => m, sec => s] = Time get()
    }

}
