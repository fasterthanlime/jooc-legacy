include stdio, stdlib, time;

const Int RANGE = 5, COUNT = 30, MAX = 500;

func main {
	
	NumberSupplier ns = new NumberSupplier(MAX);
	printf("Printing %d numbers between 0 and %d and a max of %d\n", COUNT, RANGE, MAX);
	
	for(Int i: 0..COUNT) {
		printf("%d", ns.next);
	}
	
	printf("\nThat's all, folks =)\n");
	
}

class NumberSupplier {

	Int range;

	func new(=range) {
		srand(time(null));
	}

	func next -> Int {
		return rand() % range;
	}

}
