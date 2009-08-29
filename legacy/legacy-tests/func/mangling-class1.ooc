include stdio;

class Printer {

	func print(Int value) {
		print(value, "or 42 ?");
	}
	
	func print(Int value, String message) {
		printf("%d %s\n", value, message);
	}	

}

func main {

	Printer p = new Printer;
	p.print(24);

}
