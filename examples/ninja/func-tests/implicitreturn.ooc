include stdio;

class Mine {

	Int value;
  
	new(=value);
  
	func value {
		printf("Just accessed to value!\n");
	}

}

func main {
	printf("And value is %d\n", new Mine(42).value());
}

