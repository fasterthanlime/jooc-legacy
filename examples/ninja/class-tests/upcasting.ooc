include stdio;

abstract class Animal {

	abstract func shout;

}

class Cat from Animal {

	func new;

	implement shout {
		printf("Meowwwwww!\n");
	}

}

func main {

	Animal a = new Cat;
	a.shout;
	printf("and ");
	Cat c = (Cat) a;
	c.shout;

}
