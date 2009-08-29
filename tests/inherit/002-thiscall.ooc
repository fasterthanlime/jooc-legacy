main: func {
	new Dog sayName();
}

Animal: class {

	name: String;
	sayName: func printf("Hi, my name is %s\n", name);

}

Dog: class extends Animal {

	new: func {
		this("Fido");
	}
	new: func ~withName (=name)

}
