import structs.List;
import structs.ArrayList;
import structs.SparseList;

class Torturer {
	
	List list;
	
	func new(=list);
	
	func start {
		
		printf("Torturer: testing list structure %s\n", list.class.name);
		printf("first test: appending 100 elements");
		for(Int i: 0..100) {
			list.add(new Element(i));
		}
		
	}
	
}

class Element {
	
	Int i;
	
	func new(=i);
	
}

func main {
	
	new Torturer(new ArrayList).start;
	new Torturer(new SparseList).start;
	
}
