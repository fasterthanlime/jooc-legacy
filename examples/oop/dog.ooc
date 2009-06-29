include stdio, stdlib, string;
import structs.Array;

abstract class Animal {
	
	Int age;
	String name;

}

class Dog from Animal {

	func new(=age, =name);

	func bark {
		printf("Hi, my name is %s and I'm %d years old.\n", name, age);
	}

}

func main {
	
	Dog dog = new Dog(18, "Lassie");
	dog.bark;
	printf("Done barking ! Another one ?\n");
	Dog dog2 = new Dog(21, "Rex");
	dog2.bark;
	printf("Now a dog array =D\n");
	Array dogs = new(4);
	
	for(Int i: 0..4) {
		dogs.data[i] = new Dog((i * 3) + 7, "Random Dog");
	}
	
	for(Dog dog: dogs) {
		dog.bark;
	}
	
}
