
import structs/ArrayList

A: class {
    get: func -> ArrayList<String> {
        return ArrayList<String> new()
    }
}

test: func {
    a := A new()
    for(e: String in a get()) {
        e println()
    }
}
