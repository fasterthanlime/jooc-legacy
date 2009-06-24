include stdio;

class Parent {

	static String SPECIES = "Human";

}

class Child from Parent {

	func new {
		printf("I'm of species %s\n", SPECIES);
	}

}

func main {

	printf("Parents are of species %s, and children, of species %s\n", Parent.SPECIES, Child.SPECIES);
	new Child;

}
