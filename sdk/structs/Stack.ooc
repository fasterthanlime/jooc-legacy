include memory;

/**
 * A simple stack implementation
 */
class Stack {

	Object* data;
	Int top;
	UInt size;
	
	/**
	 * Returns a new stack of max height of 32
	 * @return Stack
	 */
	func new {
		this(32);
	}
	
	/**
	 * Returns a new stack with the specified height
	 * @param UInt size Height of the stack
	 * @return Stack
	 */
	func new(=size) {
		data = malloc(size * sizeof(Object));
		if (data == null) {
			printf("Out of memory: could not allocate %d bytes\n", size * sizeof(Object));
			exit(1);
		}
		top = -1;
	}
	
	/**
	 * Grows the stack by a factor of 2
	 */
	func grow {
		data = realloc(data, size * 2 * sizeof(Object));
		if (data == null) {
			printf("Out of memory: could not allocate %d bytes\n", size * sizeof(Object));
			exit(1);
		}
		size *= 2;
	}
	
	/**
	 * Pushes an element to the top of the stack. Will grow the stack when
	 * necessary
	 * @param Object element The element to push to the stack
	 * @return Bool
	 */
	func push(Object element) -> Bool {
		if ((top + 1) >= size) {
			grow();
		}
		data[++top] = element;
		return true;
	}
	
	/**
	 * Removes the top element from the stack and returns it
	 * @return Object The element removed from the stack
	 */
	func pop -> Object {
		return (top >= 0) ? data[top--] : null;
	}
	
	/**
	 * Returns the top element of the stack without removing it
	 * @return Object The element atop the stack
	 */
	func peek -> Object {
		return (top >= 0) ? data[top] : null;
	}
	
}
