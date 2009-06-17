include stdio;

class Mine {

	Int value;
  
	new(=value);
  
	func value {
		printf("Just accessed to value!\n");
		return value;
	}

}

func main {
	printf("And value is %d\n", new Mine(42).value());
}
