import structs/List

/**
 * Resizable-array implementation of the List interface. Implements all
 * optional list operations, and permits all elements, including null.
 * In addition to implementing the List interface, this class provides
 * methods to manipulate the size of the array that is used internally
 * to store the list. (This class is roughly equivalent to Vector,
 * except that it is unsynchronized.)
 */
ArrayList: class <T> extends List {
	
	data : T*
	capacity : Int
	size = 0 : Int
	
	init: func {
		init ~withCapacity (10)
	}
	
	init: func ~withCapacity (=capacity) { 
		data = gc_malloc(capacity * T size)
	}
	
	add: func (element: T) {
		ensureCapacity(size + 1)
		data[size] = element
		size += 1
	}

	add: func ~withIndex (index: Int, element: T) {
		checkIndex(index)
		ensureCapacity(size + 1)
		dst, src: Octet*
		dst = data + (T size * (index + 1))
		src = data + (T size * index)
		bsize := (size - index) * T size
		memmove(dst, src, bsize)
		data[index] = element
		size += 1
	}

	addAll: func(list: List<T>) {
		addAll(0, list)
	}

	addAll: func~withIndex(index: Int, list: List<T>) {
		for(element: T in list) {
			add(element);
		}
	}

	clear: func {
		size = 0
	}

	get: func(index: Int) -> T {
		checkIndex(index)
		return data[index]
	}

	indexOf: func(element: T) -> Int {
		index := -1
		while(index < size) {
			index += 1
			candidate : T
			candidate = data[index]
			if(candidate == element) return index
		}
		return -1
	}

	lastIndexOf: func(element: T) -> Int {
		index := size
		while(index) {
			candidate : T
			candidate = data[index]
			if(candidate == element) return index
			index -= 1
		}
		return -1
	}

	removeAt: func (index: Int) -> T {
		element := data[index]
		memmove(data + (index * T size), data + ((index + 1) * T size), (size - index) * T size)
		size -= 1
		return element
	}

	/**
	 * Removes a single instance of the specified element from this list,
	 * if it is present (optional operation).
	 * @return true if at least one occurence of the element has been
	 * removed
	 */
	remove: func (element: T) -> Bool {
		index := indexOf(element)
		if(index == -1) return false
		else {
			removeAt(index)
		}
		return true
	}

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 */ 
	set: func(index: Int, element: T) {
		data[index] = element
	}

	/**
	 * @return the number of elements in this list.
	 */
	size: func() -> Int { size }
	
	/** 
	 * Increases the capacity of this ArrayList instance, if necessary,
	 * to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 */
	ensureCapacity: func (newSize: Int) {
		while(newSize >= capacity) {
			grow()
		}
	}

	/** private */
	grow: func {
		capacity = capacity * 1.1 + 10
		tmpData := gc_realloc(data, capacity * T size)
		if (tmpData) {
			data = tmpData
		} else {
			printf("Failed to allocate %zu bytes of memory for array to grow! Exiting..\n",
				capacity * T size)
			x := 0
			x = 1 / x
		}
	}
	
	/** private */
	checkIndex: func (index: Int) {
		if (index < 0) Exception new(This, "Index too small! " + index + " < 0") throw()
		if (index >= size) Exception new(This, "Index too big! " + index + " >= " + size()) throw()
	}
	
	iterator: func -> Iterator<T> { return ArrayListIterator<T> new(this) }
	
	clone: func -> ArrayList<T> {
		copy := This<T> new(size())
		copy addAll(this)
		return copy
	}
	
}

ArrayListIterator: class <T> extends Iterator {

	list: ArrayList<T>
	index := 0
	
	init: func(=list) {}
	
	hasNext: func -> Bool {
		return index < list size()
	}
	
	next: func -> T {
		element := list get(index)
		index += 1
		return element
	}
	
}

/** Operators */
operator [] <T> (list: ArrayList<T>, i: Int) -> T { list get(i) }
operator []= <T> (list: ArrayList<T>, i: Int, element: T) { list set(i, element) }
operator += <T> (list: ArrayList<T>, element: T) { list add(element) }
operator -= <T> (list: ArrayList<T>, element: T) -> Bool { list remove(element) }

