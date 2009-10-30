import structs/List

/** 
 * LinkedList, not tested, use at your own risk!
 * @author eagle2com
 */

	
LinkedList: class <T> extends List<T> {
	
	size = 0 : Int
	first: Node<T>
	last: Node<T>
	
	init: func {
		first = null
		last = null
	}
	
	add: func (data: T) {
		node: Node<T>
		if(first) {
			node = Node<T> new(last,null,data)
			last next = node
		} else {
			node = Node<T> new(null,null,data)
			first = node
		}
		last = node
		size += 1
	}
	
	add: func ~withIndex(index: Int, data: T) {
		if(index > 0) {
			prevNode := getNode(index - 1)
			nextNode := prevNode next
			node := Node<T> new(prevNode,nextNode,data)
			prevNode next = node
			nextNode prev = node
		} else if (index == 0) {
			node := Node<T> new(null,first,data)
			if(first) {
				first prev = node
				first = node
			} else {
				first = node
				last = node
			}
		} else {
			Exception new(This, "Check index: 0 <= " + index + " < " + size) throw()
		}
	}
	
	clear: func {
	}
	
	get: func(index: Int) -> T {
		return getNode(index) data
	}
	
	getNode: func(index: Int) -> Node<T> {
		if(index < 0 || index >= size) {
			Exception new(This, "Check index: 0 <= " + index + " < " + size) throw()
		}
		
		i = 0 : Int
		current := first
		while(current next && i < index) {
			current = current next
			i += 1
		}
		return current
	}
	
	indexOf: func (data: T) -> Int {return 0}
	
	lastIndexOf: func (data: T) -> Int {return 0}
	
	removeAt: func (index: Int) -> T {
		if(first && index >= 0 && index < size) {
			toRemove := getNode(index)
			if(toRemove next) {
				toRemove next prev = toRemove prev
			} else {
				last = toRemove prev
				if(toRemove prev) {
					toRemove prev next = null
				}
			}
			
			if(toRemove prev) {
				toRemove prev next = toRemove next
			} else {
				first = toRemove next
				if(toRemove next) {
					toRemove next prev = null
				}
			}
			size -= 1
			return toRemove data
		} else {
			Exception new(This, "Check index: 0 <= " + index + " < " + size) throw()
		}
	}
	
	remove: func (data: T) -> Bool {return false}
	
	set: func (index: Int, data: T) {}
	
	size: func -> Int {return size}
	
	iterator: func -> Iterator<T> {
		LinkedListIterator new(this)
	}
	
	clone: func -> LinkedList<T> {return 0}
} 






Node: class <T>{
	
	prev: Node<T>
	next: Node<T>
	data: T
	
	init: func {
	}
	
	init: func ~withParams(=prev, =next, =data) {}
	
}

LinkedListIterator: class <T> extends Iterator<T>  {
	
	current: Node<T>
	
	init: func(list: LinkedList<T>) {
		current = list first
	}
	
	hasNext: func -> Bool {
		if(current)
			return true
			 
		return false
	}
	 
	next: func -> T {
		prev := current
		current = current next
		return prev data
	}
	
}


main: func {
	list1 := LinkedList<Int> new()

	for(i in 0..9){
		list1 add(i)
	}
	
	list1 removeAt(0)
	list1 removeAt(5)
		
	iter := list1 iterator()
	i = 0 : Int
	
	while(iter hasNext()) {
		printf("| %d |", iter next())
		i += 1
	}
	
	println()
}
