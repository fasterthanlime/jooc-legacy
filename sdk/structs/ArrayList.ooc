include stdlib, stdio, memory;

import List, Iterator, Iterable;

/**
 * Resizable-array implementation of the List interface. Implements all
 * optional list operations, and permits all elements, including null.
 * In addition to implementing the List interface, this class provides
 * methods to manipulate the size of the array that is used internally
 * to store the list. (This class is roughly equivalent to Vector,
 * except that it is unsynchronized.)
 */
class ArrayList from List {
	
	Object* data;
	Int capacity;
	Int size;

	func new {
		this(10);
	}

	func new(=capacity) {
		size = 0;
		data = malloc(capacity * sizeof(Object));
	}
	
	func size {
		return size;
	}
	
	func capacity {
		return capacity;
	}
	
	/**
	 * Appends the specified element to the end of this list.
	 */
	func add(Object element) {
		ensureCapacity(size + 1);
		data[size++] = element;
	}

	/**
	 * Inserts the specified element at the specified position in
	 * this list. 
	 */
	func add(Int index, Object element) {
		if(!isValidIndex(index)) {
			Int x = 0;
			x = 1 / x;
			return;
		}
		ensureCapacity(size + 1);
		memmove(data+index+1, data+index, (size-index) * sizeof(Object));
		data[index] = element;
		size++;
	}
	
	/**
	 * Appends all of the elements in the specified Collection to the
	 * end of this list, in the order that they are returned by the
	 * specified Collection's Iterator.
	 */
	func addAll(ArrayList list) {
		addAll(0, list);
	}
	
	/**
	 * Inserts all of the elements in the specified Collection into
	 * this list, starting at the specified position.
	 */
	func addAll(Int index, ArrayList list) {
		ensureCapacity(size + list.size());
		memmove(data+index+list.size(), data+index, (size-index) * sizeof(Object));
		for(Int i: 0..list.size) {
			data[index+i] = list.get(i);
		}
	}
	
	/**
	 * Removes all of the elements from this list.
	 */
	func clear {
		size = 0;
	}
	
	/**
	 * @return a shallow copy of this ArrayList instance.
	 */
	func clone -> ArrayList {
		ArrayList clone = new ArrayList(size);
		clone.addAll(this);
		return clone;
	}
	
	/** 
	 * Increases the capacity of this ArrayList instance, if necessary,
	 * to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 */
	func ensureCapacity(Int newSize) {
		while(newSize >= capacity) {
			grow();
		}
	}
	
	/**
	 * @return the element at the specified position in this list.
	 */
	func get(Int i) -> Object {
		if(!isValidIndex(i)) {
			Int x = 0;
			x = 1 / x;
			return null;
		}
		return data[i];
	}
	
	/**
	 * @return the index of the first occurence of the given argument,
	 * (testing for equality using the equals method), or -1 if not found
	 */
	func indexOf(Object element) -> Int {
		Int result = -1;
		for(Int i: 0..size) {
			if(data[i] == element) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	/**
	 * @return the index of the last occurrence of the specified object
	 * in this list.
	 */
	func lastIndexOf(Object element) -> Int {
		Int result = -1;
		for(Int i = size - 1; i >= 0; i--) {
			if(data[i] == element) {
				result = i;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Removes the element at the specified position in this list.
	 * @return the element just removed
	 */
	func remove(Int i) -> Object {
		if(!isValidIndex(i)) {
			Int x = 0;
			x = 1 / x;
			return null;
		}
		Object element = data[i];
		memmove(data+i, data+i+1, (size-i) * sizeof(Object));
		size--;
		return element;
	}
	
	/**
	 * Removes a single instance of the specified element from this list,
	 * if it is present (optional operation).
	 * @return true if at least one occurence of the element has been
	 * removed
	 */
	func removeElement(Object element) -> Bool {
		Bool result;
		if(element != null) {
			Int i = indexOf(element);
			if(i != -1) {
				remove(i);
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}
	
	/** private */
	func isValidIndex(Int i) -> Bool {
		if(i < 0) {
			printf("ArrayList: ArrayIndexOutOfBoundException: index=%d < 0\n", i);
			return false;
		} else if(i >= size) {
			printf("ArrayList: ArrayIndexOutOfBoundException: index=%d >= size=%d\n", i, size);
			return false;
		}
		return true;
	}

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 */ 
	func set(Int i, Object element) {
		if(!isValidIndex(i)) {
			Int x = 0;
			x = 1 / x;
			return;
		}
		data[i] = element;
	}

	/** private */
	func grow {
		capacity = capacity * 1.1 + 10;
		Object* tmpData = realloc(data, capacity * sizeof(Object));
		if(tmpData != null) {
			data = tmpData;
		} else {
			printf("Failed to allocate %d bytes of memory for array to grow! Exiting..\n", capacity * sizeof(Object));
			exit(1);
		}
	}
	
	implement iterator {
		return new ArrayListIterator(this);
	}
	
}

class ArrayListIterator from Iterator {

	ArrayList list;
	Int index = 0;
	
	func new(=list);
	
	implement hasNext {
		return index < list.size;
	}
	
	implement next {
		return list.data[index++];
	}
	
}
