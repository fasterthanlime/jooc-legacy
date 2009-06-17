include stdio;

abstract class Animal {

	abstract func shout;

}

class Cat from Animal {

	new();

	implement shout {
		printf("Meowwwwww!\n");
	}

}

func main {

	new Cat.shout;

}
