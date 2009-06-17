include stdio, stdlib;

import structs.List;
import structs.ArrayList;
import structs.SparseList;
import lang.Integer;

class Tests {
	
	void printTitle(String title) {
		printf("\n\t==================================================");
		printf("\n\t= %s =", title);
		printf("\n\t==================================================\n\n");
	}
	
	void test() {
		
		printTitle("Testing ArrayList");
		testList(new ArrayList());
		testIntegerList(new ArrayList());
		
		printTitle("Testing SparseList");
		testList(new SparseList());
		testIntegerList(new SparseList());
		
	}
	
	void printIntList(List list) {
		
		printf("size/capa = %d/%d: ", list.size(), list.capacity());
		printf("[");
		for(int i: 0..list.size()) {
			int *element = list.get(i);
			printf("%d", *element);
			if(i + 1 < list.size()) {
				printf(", ");
			}
		}
		printf("]\n");
		
	}

	void testList(List list) {
		
		printf("initial size/capa = %d/%d\n", list.size(), list.capacity());
		
		
		printf("adding numbers 0 to 11 included:\n");
		for(int i: 0..12) {
			int *element = malloc(sizeof(int));
			*element = i;
			list.add(element);
		}
		printIntList(list);

		printf("adding element '69' at 5\n");
		{
			int *element = malloc(sizeof(int));
			*element = 69;
			list.add(5, element);
		}
		printIntList(list);
		
		
		
		printf("setting element to '56' at 2\n");
		{
			int *element = malloc(sizeof(int));
			*element = 56;
			list.set(2, element);
		}
		printIntList(list);
		
		printf("removing 3 times the element at '7'\n");
		for(int i: 0..3) {
			list.remove(7);
		}
		printIntList(list);
		
		printf("adding element '3' at 7\n");
		{
			int *element = malloc(sizeof(int));
			*element = 3;
			list.add(7, element);
		}
		printIntList(list);
		
		printf("last index of 4th element ? = %d\n", list.lastIndexOf(list.get(4)));
		
		for(int i: 0..list.size()) {
			printf("index of #%d element ? = %d\n", i, list.indexOf(list.get(i)));
		}
		
		printf("contains null ? %d\n", list.contains(null));
		printf("index of null ? %d\n", list.indexOf(null));
		
		printf("clearing...");
		list.clear();

		printf("list size/capacity = %d/%d\n", list.size(), list.capacity());
		
	}
	
	void testIntegerList(List list) {

		list.add(new Integer(42));
		Object o = list.get(0);
		printf("list.get(0) = %d\n", ((Integer) o).getValue());
		
	}
}

int main() {
	Tests tests = new Tests();
	tests.test();
	return 0;
}
