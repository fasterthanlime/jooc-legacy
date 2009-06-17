include stdio;

class Blah {

	Int field1;
	Char field2;

	new() {
		field1 = 23;
		field2 = '$';
	}

	func stuff {
		printf("Hoy! our class is %s\n", this.class->name);
	}

}

func main {

	new Blah.stuff;

}
