include stdio;

class Nightmare {

	Int value;
	
	new(=value);

	func getThis -> Nightmare {
		return this;
	}
	
	func shout {
		printf("I shout %d \n", value);
	}

}

func shout(Nightmare n) {
	
	printf("And the Nightmare said:\n\t");
	n.shout;
	
}

func main {

	new Nightmare(42).getThis.getThis.getThis.getThis.shout;
	shout(new Nightmare(69).getThis);

}
