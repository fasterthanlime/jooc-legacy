main: func {
	new Dog sayName();
}

Animal: class {

	name: String;
	new: func ~withName (=name)
	sayName: func printf("Hi, my name is %s\n", name);

}

Dog: class extends Animal {

	new: func {
		super("Fido");
	}

}
