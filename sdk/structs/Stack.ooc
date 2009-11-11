import structs/ArrayList

Stack: class <T> extends Iterable<T> {
	data: ArrayList<T>
	
	init: func() {
		data = ArrayList<T> new()
	}

	push: func(element: T) {
		data add(element)
	}
	
	pop: func() -> T {				
		if (isEmpty())
			Exception new(This, "Trying to pop an empty stack.") throw()
			
		return data removeAt(lastIndex())
	}
    
    pop: func ~checked (value: T) -> T {
        real := pop()
        if(real != value)
            Exception new(This, "Trying to pop %p, but %p came out.") throw()
        return real
    }
	
	peek: func() -> T {
		if (isEmpty())
			Exception new(This, "Trying to peek an empty stack.") throw()
			
		return data get(lastIndex())
	}
	
	size: func() -> Int {	
		return data size()
	}
	
	isEmpty: func() -> Bool {
		return size() <= 0
	}
	
	lastIndex: func() -> Int {
		return size() - 1
	}
    
    clear: func {
        data clear()
    }
    
    iterator: func -> Iterator<T> { data iterator() }
}
