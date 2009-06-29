include stdio, stdlib;

class Class {
	
	Int a;
	Double b;
	Char c;
	
	func new(=a, =b, =c);

	func new {
		this(12, 32.4, 'f');
	}

	func print {
		printf("%d, %f, %c\n", a, b, c);
	}
}

func main {
	new Class.print;
}
