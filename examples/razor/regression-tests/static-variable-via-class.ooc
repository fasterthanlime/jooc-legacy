include stdio;

class Ant {

	static Int count;
	Int number;

	new() {
		number = Ant.count++;
		printf("Just created ant #%d\n", number);
	}

}

func main {

	printf("Beginning to proliferate...\n");
	for(Int i: 0..10) {
		new Ant;
	}
	printf("Invasion complete!\n");

}
