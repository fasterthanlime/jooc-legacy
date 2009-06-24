include stdio;

class Mine {
	
	Int value;
	
	func doStuff(value) {
		printf("value is an int, and it equals %d\n", value);
	}
	
}

func main {
	new Mine().doStuff(1995);
}
