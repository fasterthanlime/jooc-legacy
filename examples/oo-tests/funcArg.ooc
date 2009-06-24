include stdio, stdlib;

class Dummy {

	Int value;

	func new(=value);

	func method {
		this.method2;
	}

	func method2 {
		printf("Value = %d\n", value);
	}

}

func main {

	Dummy dum = new Dummy(42);
	dum.method;

}
