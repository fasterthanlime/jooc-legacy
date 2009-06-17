include stdio, stdlib;

class Class {
	
	Int a;
	Double b;
	Char c;
	
	new(=a, =b, =c);

	new() {
		this(12, 32.4, 'f');
	}

	func print {
		printf("%d, %f, %c\n", a, b, c);
	}
}

func main {
	new Class.print;
}
