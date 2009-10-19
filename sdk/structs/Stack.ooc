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
	
	peek: func() -> T {
		if (isEmpty())
			Exception new(This, "Trying to peek an empty stack.") throw()
			
		return data[lastIndex()]
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
    
    iterator: func -> Iterator<T> { data iterator() }
}
