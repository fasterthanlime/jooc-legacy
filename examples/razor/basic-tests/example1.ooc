include stdio, stdlib;

class MyObject {

	String name;

	new(=name);

	func sayHello {
		printf("Hi dude, my name is %s, and I'm happy to meet you!\n", name);
		printfd();
	}

}

func main {

	printf("Trying to create object..\n");
	MyObject object = new MyObject("Smith");
	printf("Trying to say hello\n");
	object.sayHello;
	printf("Returning\n");
	return 0;

}
