import structs/HashMap

HashBag: class {

    myMap: HashMap<Cell<Pointer>>
    
    init: func {
        init ~withCapacity(10)
    }

    init: func ~withCapacity(capacity: Int) {
        myMap = HashMap<Cell<Pointer>> new(capacity)
    }

    get: func <T> (key: String, T: Class) -> T {
        return getEntry(key, T) value
    }

    getEntry: func <T> (key: String, T: Class) -> HashEntry<Pointer> {
        entry := myMap getEntry(key)
        if (entry) {
            cell := entry value as Cell<T>
            return HashEntry<T> new(key, cell val)
        }
        return HashEntry<T> new(key, None new())
    }

    put: func <T>(key: String, value: T) -> Bool {
        tmp := Cell<T> new(value)
        return myMap put(key, tmp)
    }

    add: func <T> (key: String, value: T) -> Bool {
        return put(key, value)
    }

    isEmpty: func -> Bool {return myMap isEmpty()}
    
    contains: func <T> (key: String, T: Class) -> Bool {
        return myMap get(key) ? true : false
    }

    remove: func (key: String) -> Bool {
        return myMap remove(key)
    }

    size: func -> Int {myMap size}

    exists: func(key: String) -> Bool {
        myMap get(key) T != None
    }
}

