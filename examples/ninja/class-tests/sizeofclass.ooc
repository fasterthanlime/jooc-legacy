include stdio;

abstract class Animal {

	String name;

	abstract func shout;

}

class Cat from Animal {

	String color;

	new();

	implement shout {
		printf("Meowwwwww!\n");
	}

}

func main {

	Cat c = new Cat;
	Animal a = (Animal) c;
	printf("Size of animal: %d, size of cat: %d\n", sizeof(*a), sizeof(*c));

}
