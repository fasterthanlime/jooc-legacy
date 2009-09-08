/**
 * List interface for a data container
 */
List: abstract class<T> extends Iterable {

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

	iterator: abstract func -> Iterator
}