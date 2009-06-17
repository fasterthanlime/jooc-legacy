include stdio, stdlib;
import structs.Iterable;
import structs.Iterator;

class Array from Iterable {

	Int length;
	Object* data;
	
	new(=length, =data);
	
	func get(Int i) -> Object {
		if(i < 0 || i >= length) {
			printf("Out of bounds exception: Attempting to access an array of size %d at index %d\n", length, i);
			exit(1);
		}
		return data[i];
	}
	
	implement iterator {
		new ArrayIterator(this);
	}

}

class ArrayIterator from Iterator {
	
	Array array;
	Int i = 0;
	
	new(=array);
	
	implement hasNext {
		i < array.length;
	}
	
	implement next {
		array.get(i++);
	}
	
}
