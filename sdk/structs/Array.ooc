import lang/String

Array: class <T> extends Iterable {

	size: SizeT
	data: T*
	
	init: func (=size) {
		data = gc_calloc(size, Pointer size)
	}
	
	// FIXME .data should work!
	init: func ~withData (data: Pointer, =size) {
		this data = gc_calloc(size, T size)
		memcpy(this data, data, size * T size)
	}
	
	get: func (i: Int) -> T {
		if(i < 0 || i >= size) {
			Exception new(This, "Attempting to access an array of size " append(
				size as Int toString()) append(" at index ") append(i toString()) append("\n")) throw()
		}
		return data[i]
	}
	
	set: func (i : Int, value: T) {
		if(i < 0 || i >= size) {
			Exception new(This, "Attempting to set the value of an array of size " append(
				size as Int toString()) append(" at index ") append(i toString()) append("\n")) throw()
		}
		data[i] = value
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
	
	isEmpty: func -> Bool {
		return size == 0
	}
	
	each: func (f: Func (T)) {
		for(i in 0..size) {
			// FIXME f(get(i)) // is buggy. =D
			val := get(i)
			f(val)
		}
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

operator [] <T> (arr: Array<T>, index: Int) -> T {
	return arr get(index)
}

operator []= <T> (arr: Array<T>, index: Int, value: T) {
	arr set(index, value)
}

