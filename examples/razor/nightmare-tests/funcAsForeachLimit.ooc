include stdlib, stdio;

class Dummy {

	Dummy me;

	new() {
		me = this;
	}

	func func1 -> Int {
		return 3;
	}
	
	func func2 -> Int {
		return 0;
	}

}

func main {
	
	Dummy dummy = new Dummy;
	
	for(Int i: 0..(dummy.me.me.me.func1)) {
		printf("0 to parenthezised func, %d\n", i);
	}
	
	for(Int i: 0..dummy.func1) {
		printf("0 to freestyle func, %d\n", i);
	}
	
	for(Int i: (dummy.func2)..3) {
		printf("parenthezised func2 to 3, %d\n", i);
	}
	
	for(Int i: dummy.func2..3) {
		printf("freestyle func2 to 3, %d\n", i);
	}
	
	for(Int i: (dummy.func2)..(dummy.func1)) {
		printf("parenthezised func2 to parenthezised func, %d\n", i);
	}
	
	for(Int i: dummy.func2..dummy.func1) {
		printf("freestyle func2 to freestyle func, %d\n", i);
	}
	
	for(Int i: (0..3)) {
		printf("parenthesized range, %d\n", i);
	}
	
	for(Int i: 0..3) {
		printf("freestyle range, %d\n", i);
	}
}
