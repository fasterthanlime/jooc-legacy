/**
 * List interface for a data container
 */
List: abstract class <T> extends Iterable {

	/**
	 * Appends the specified element to the end of this list.
	 */
	add: abstract func(element: T)
	
	/**
	 * Inserts the specified element at the specified position in
	 * this list. 
	 */
	add: abstract func~withIndex(index: Int, element: T)
	
	/**
	 * Appends all of the elements in the specified Collection to the
	 * end of this list, in the order that they are returned by the
	 * specified Collection's Iterator.
	 */
	addAll: func(list: List<T>) {
		//addAll(0, list)
	}
	
	/**
	 * Inserts all of the elements in the specified Collection into
	 * this list, starting at the specified position.
	 */
	addAll: func~withIndex(index: Int, list: List<T>) {
		for(element: T in list) {
			add(element);
		}
	}
	
	/**
	 * Removes all of the elements from this list.
	 */
	clear: abstract func()

	/**
	 * Removes the last element of the list, if any (=non-empty list).
	 * @return true if it has removed an element, false if the list
	 * was empty.
	 */
	/*func removeLast -> Bool {
		Int size = size();
		if(size > 0) {
			remove(size - 1);
			return true;
		} else {
			return false;
		}
	}*/
	
	/**
	 * @return true if this list contains the specified element.
	 */
	contains: func(element: T) -> Bool {
		return indexOf(element) != -1
	}
	
	/**
	 * @return the element at the specified position in this list.
	 */
	get: abstract func(index: Int) -> T
	
	/**
	 * @return the index of the first occurence of the given argument,
	 * (testing for equality using the equals method), or -1 if not found
	 */
	indexOf: abstract func(element: T) -> Int
	
	/**
	 * @return true if this list has no elements.
	 */
	isEmpty: func() -> Bool {
		return (size() == 0);
	}
	
	/**
	 * @return the index of the last occurrence of the specified object
	 * in this list.
	 */
	lastIndexOf: abstract func(element: T) -> Int
	
	/**
	 * Removes the element at the specified position in this list.
	 * @return the element just removed
	 */
	remove: abstract func(index: Int) -> T
	
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

	iterator: abstract func -> Iterator<T>
	
}
