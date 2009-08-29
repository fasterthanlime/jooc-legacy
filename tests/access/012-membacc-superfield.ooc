main: func {

	new Dog("Dogbert") print()

}

Animal: class {

	name: String
	new: func(=name)

}

Dog: class extends Animal {

	new: func(.name) {
		super(name)
	}

	print: func {
		printf("My name is %s\n", this name)
	}

}
