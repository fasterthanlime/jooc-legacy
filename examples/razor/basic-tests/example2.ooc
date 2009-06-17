include stdio, stdlib;

class MyObject {

	String name;

	new(=name);
	
	func sayHello {
		printf("Hi dude, my name is %s, and I'm happy to meet you!\n", name);
	}

}

class MyObject2 from MyObject {

	String surname;

	new(=name, =surname);

	override sayHello {
		printf("Hi sweetie, my name really is %s, but you can call me %s if you like.\n", name, surname);
	}


}

func main {

	MyObject2 object2 = new MyObject2("Andrew", "Andy");
	object2.sayHello;

	MyObject object = (MyObject) object2;
	object.sayHello;

	MyObject otherMyObject = new MyObject("Smith");
	otherMyObject.sayHello;

}
