include stdio;

class Printer {

	func print(Int intValue) {
		printf("Int: %d\n", intValue);
	}
	
	func print(Float floatValue) {
		printf("Float: %.2ff\n", floatValue);
	}

}

func main {
	Printer p = new Printer;
	p.print(42);
	p.print(3.14f);
}
