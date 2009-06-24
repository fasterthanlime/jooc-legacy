include stdio, stdlib;
import structs.Iterable;
import structs.Iterator;

class Array from Iterable {

	Int size;
	Object* data;
	
	func new(=size) {
		data = calloc(size, sizeof(Object));
	}
	
	func new(=size, =data);
	
	func get(Int i) -> Object {
		if(i < 0 || i >= size) {
			printf("Out of bounds exception: Attempting to access an array of size %d at index %d\n", size, i);
			exit(1);
		}
		return data[i];
	}

	static func nullTerminated(Object* p) -> Array {
		Object* q = p;
		while(*q) q++;
		return new Array(q - p, p);
	}
	
	implement iterator {
		(Iterator) new ArrayIterator(this);
	}

}

class ArrayIterator from Iterator {
	
	Array array;
	Int i = 0;
	
	func new(=array);
	
	implement hasNext {
		i < array.size;
	}
	
	implement next {
		array.get(i++);
	}
	
}
