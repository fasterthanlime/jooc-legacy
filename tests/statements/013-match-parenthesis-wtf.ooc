Blah: class {

    init: func() {
    	// FIXME: unless there are parenthesis around test(), j/ooc won't compile it.
	// this is a bug, but a non-trivial fix for j/ooc, so it should at least work
	// in rock.
        match test() {
            case true => return
            case false => return
        }
    }

    test: func() -> Bool {
        return true
    }
}

main: func {}
