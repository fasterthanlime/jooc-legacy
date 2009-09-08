import structs.List

/**
 * Resizable-array implementation of the List interface. Implements all
 * optional list operations, and permits all elements, including null.
 * In addition to implementing the List interface, this class provides
 * methods to manipulate the size of the array that is used internally
 * to store the list. (This class is roughly equivalent to Vector,
 * except that it is unsynchronized.)
 */
ArrayList: class<T> extends List {
	
	data: T*
	capacity: Int
	size: Int
	
	init: func() {
		this(10)
	}
	
	init: func~withCapacity(=capacity) { 
		size = 0
		data = gc_malloc(capacity * T size)
	}
	
	add: func(element: T) {

	}

	add: func~withIndex(index: Int, element: T) {
	
	}

	addAll: func(list: List<T>) {
		//addAll(0, list)
	}

	addAll: func~withIndex(index: Int, list: List<T>) {
		for(element: T in list) {
			add(element);
		}
	}

	clear: func() {
	
	}

	get: func(index: Int) -> T {
	
	}

	indexOf: func(element: T) -> Int {
	
	}

	lastIndexOf: func(element: T) -> Int {
	
	}

	remove: func(index: Int) -> T {
	
	}

	/**
	 * Removes a single instance of the specified element from this list,
	 * if it is present (optional operation).
	 * @return true if at least one occurence of the element has been
	 * removed
	 */
	removeElement: abstract func(element: T) -> Bool 

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 */ 
	set: abstract func(index: Int, element: T)

	/**
	 * @return the number of elements in this list.
	 */
	size: abstract func() -> Int
}