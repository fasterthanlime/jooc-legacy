import structs/ArrayList

Stack: class<T> {
	data: ArrayList<T>
	
	init: func() {
		data = ArrayList<T> new()
	}

	push: func(element: T) {
		data add(element)
		printf("adding: %d\n", element)
	}
	
	pop: func() -> T {				
		if (isEmpty())
			Exception new(This, "Empty stack, cannot pop.") throw()
						
		ret := data get(size() - 1) as T
		data removeAt(size() - 1)
		return ret
	}
	
	peek: func() -> T {
		if (isEmpty())
			Exception new(This, "Empty stack, cannot peek.") throw()
			
		return data[size() - 1]
	}
	
	size: func() -> Int {	
		return data size()
	}
	
	isEmpty: func() -> Bool {
		return size() <= 0
	}
 }

main: func() {
	"Creating stack" println()
	stack := Stack<Int> new()
	printf("Size: %d\n", stack size())
		
	"Pushing 10 items on the stack" println()
	for (i: Int in 0..10)
		stack push(i)
	
	printf("Size: %d\n", stack size())
	stack pop()
	printf("Size: %d\n", stack size())
	
}