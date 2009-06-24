include stdio;

func main {

	new MyOhMy.blowUp;

}

class MyOhMy {

	MyOhMy darn;

	new() {
		// gosh! we haven't initialized!!
		darn = null;
	}

	func blowUp {
		darn.cry;
	}

	func cry {
		printf("I don't even exist, how can I cry? How sad :(\n");
	}

}
