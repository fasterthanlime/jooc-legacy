include stdio;

func sneazy {

	printf("aaaaaaaahhh TCHOOOOOH!\n");

}

class Test {

	Func finkel = @sneazy;

	func new {
		@finkel = @sneazy;
	}

	func shoosh {
		this.finkel;
	}

}

func main {
	
	new Test.shoosh;
	
}
