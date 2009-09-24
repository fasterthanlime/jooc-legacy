
Array: class <T> extends Iterable {

	size: SizeT
	data: T*
	
	init: func (=size) {
		data = gc_calloc(size, Pointer size)
	}
	
	init: func ~withData (=size, .data) {
		// this "&" shouldn't be needed.. hmm.
		//this data = data&
		this data = gc_calloc(size, T size)
		memcpy(this data, data&, size * T size)
	}
	
	get: func (i: Int) -> T {
		if(i < 0 || i >= size) {
			Exception new(This, "Attempting to access an array of size " + size as Int + " at index " + i + "\n") throw()
		}
		return data[i]
	}
	
	size: func -> Int {
		return size
	}

	/*
	nullTerminated: static func (p : T*) -> Array {
		Object* q = p;
		while(*q) q++;
		return new Array(q - p, p);
	}
	*/
	
	iterator: func -> Iterator<T> {
		return ArrayIterator<T> new(this);
	}

	lastIndex: func -> SizeT {
		return size - 1
	}

}

ArrayIterator: class <T> extends Iterator {
	
	array: Array
	i := 0
	
	init: func (=array) {}
	
	hasNext: func -> Bool {
		return i < array size
	}
	
	next: func -> T {
		value := array get(i)
		i += 1
		return value
	}
	
}
