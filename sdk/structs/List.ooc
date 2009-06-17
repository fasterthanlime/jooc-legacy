/**
 * List interface for a data container
 */
abstract class List {
	
	/**
	 * Appends the specified element to the end of this list.
	 */
	abstract func add(Object element);

	/**
	 * Inserts the specified element at the specified position in
	 * this list. 
	 */
	abstract func add(Int index, Object element);
	
	/**
	 * Appends all of the elements in the specified Collection to the
	 * end of this list, in the order that they are returned by the
	 * specified Collection's Iterator.
	 */
	func addAll(List list) {
		addAll(0, list);
	}
	
	/**
	 * Inserts all of the elements in the specified Collection into
	 * this list, starting at the specified position.
	 */
	func addAll(Int index, List list) {
		for(Int i: 0..list.size()) {
			add(list.get(i));
		}
	}
	
	/**
	 * Removes all of the elements from this list.
	 */
	abstract func clear;
	
	/**
	 * Removes the last element of the list, if any (=non-empty list).
	 * @return true if it has removed an element, false if the list
	 * was empty.
	 */
	func removeLast -> Bool {
		Int size = size();
		if(size > 0) {
			remove(size - 1);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @return true if this list contains the specified element.
	 */
	func contains(Object element) -> Bool {
		return (indexOf(element) != -1);
	}
	
	/**
	 * @return the element at the specified position in this list.
	 */
	abstract func get(Int i) -> Object;
	
	/**
	 * @return the index of the first occurence of the given argument,
	 * (testing for equality using the equals method), or -1 if not found
	 */
	abstract func indexOf(Object element) -> Int;
	
	/**
	 * @return true if this list has no elements.
	 */
	func isEmpty -> Bool {
		return (size() == 0);
	}
	
	/**
	 * @return the index of the last occurrence of the specified object
	 * in this list.
	 */
	abstract func lastIndexOf(Object element) -> Int;
	
	/**
	 * Removes the element at the specified position in this list.
	 * @return the element just removed
	 */
	abstract func remove(Int i) -> Object;
	
	/**
	 * Removes a single instance of the specified element from this list,
	 * if it is present (optional operation).
	 * @return true if at least one occurence of the element has been
	 * removed
	 */
	abstract func removeElement(Object element) -> Bool;

	/**
	 * Replaces the element at the specified position in this list with
	 * the specified element.
	 */ 
	abstract func set(Int i, Object element);
	
	/**
	 * @return the number of elements in this list.
	 */
	abstract func size -> Int;

	/**
	 * @return the number of elements this list can hold without needing
	 * to grow.
	 */
	abstract func capacity -> Int;

}
