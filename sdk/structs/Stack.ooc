import structs/ArrayList

Stack: class<T> {
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
			
		return data removeAt(size() - 1)
	}
	
	peek: func() -> T {
		if (isEmpty())
			Exception new(This, "Trying to peek an empty stack.") throw()
			
		return data[size() - 1]
	}
	
	size: func() -> Int {	
		return data size()
	}
	
	isEmpty: func() -> Bool {
		return size() <= 0
	}
}
