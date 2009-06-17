include stdio, stdlib, string;

abstract class Animal {
	
	Int age;
	String name;

	abstract func shout;

}

class Dog from Animal {

	new(=age, =name);

	implement shout {
		printf("Woof woof! I'm %s the dog, and I'm %d years old.\n", name, age);
	}

}

class Cat from Animal {

	new(=age, =name);

	implement shout {
		printf("Meooowww, my name is %s and I'm a sweet %d-year-old cat.\n", name, age);
	}

}

func main {

	printf("## Creating dog and cat.\n");
	Dog dog = new Dog(18, "Lassie");
	Cat cat = new Cat(12, "Grok");

	printf("## Making them both shout the straightforward way.\n");
	dog.shout;
	cat.shout;

	printf("## Casting to animal and making them shout.\n");
	((Animal) dog).shout;
	((Animal) cat).shout;

	printf("## Storing in array and making shout\n");
	Animal* animals = calloc(2, sizeof(animals));
	animals[0] = (Animal) dog;
	animals[1] = (Animal) cat;
	for(Int i: 0..2) {
		animals[i].shout;
	}

}
